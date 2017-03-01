package com.jd.si.jupiter.smart.handler;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.jd.si.jupiter.smart.codec.ThriftMessage;
import com.jd.si.jupiter.smart.core.*;
import com.jd.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.jd.si.jupiter.smart.duplex.TProtocolPair;
import com.jd.si.jupiter.smart.duplex.TTransportPair;
import com.jd.si.jupiter.smart.processor.SmartProcessorFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.*;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkState;
/**
 * 负责将TSmartTransport分发给TProcessor,并像客户端负责回写TProcessor响应
 *
 * 注意:所有的异步客户端都有能力一次发送多个请求,但是不能处理这些请求的多个响应(应为不知道谁是谁的响应)
 */
public class SmartDispatcher extends SimpleChannelInboundHandler {
    private final SmartProcessorFactory processorFactory;
    private final Executor dispatcherExecutor;
    private final long taskTimeoutMillis;
    private final Timer taskTimeoutTimer;
    private final long queueTimeoutMillis;
    private final int queuedResponseLimit;
    private final Map<Integer, ThriftMessage> responseMap = new HashMap<Integer, ThriftMessage>();
    private final AtomicInteger dispatcherSequenceId = new AtomicInteger(0);
    private final AtomicInteger lastResponseWrittenId = new AtomicInteger(0);
    private final TDuplexProtocolFactory duplexProtocolFactory;

    public SmartDispatcher(ThriftServerDef def, Timer timer) {
        this.processorFactory = def.getProcessorFactory();
        this.duplexProtocolFactory = def.getDuplexProtocolFactory();
        this.queuedResponseLimit = def.getQueuedResponseLimit();
        this.dispatcherExecutor = def.getExecutor();
        this.taskTimeoutMillis = def.getTaskTimeout();
        this.taskTimeoutTimer = (def.getTaskTimeout() >= 0 ? null : timer);
        this.queueTimeoutMillis = def.getQueueTimeout();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message)
            throws Exception {
        System.out.println("====="+this.getClass().getSimpleName());
        if (message instanceof ThriftMessage) {
            ThriftMessage thriftMessage = (ThriftMessage) message;
            thriftMessage.setProcessStartTimeMillis(System.currentTimeMillis());
            //根据第一个request请求判定是否对所有响应有序
            checkResponseOrderingRequirements(ctx, thriftMessage);
            //虚拟TTransport,将netty的处理数据封装进TTransport
            TSmartTransport messageTransport = new TSmartTransport(ctx.channel(), thriftMessage);
            //TTransportPair 负责将输入输出的transport组合到一起
            TTransportPair transportPair = TTransportPair.fromSingleTransport(messageTransport);
            TProtocolPair protocolPair = duplexProtocolFactory.getProtocolPair(transportPair);
            TProtocol inProtocol = protocolPair.getInputProtocol();
            TProtocol outProtocol = protocolPair.getOutputProtocol();

            processRequest(ctx, thriftMessage, messageTransport, inProtocol, outProtocol);
        } else {//如果不是thriftMessage重新向下传递
            ctx.fireChannelRead(message);
        }
    }


    //取消父类当中的重写回调channelRead0 ,该方法目前这里什么也不处理
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    /**
     * 判断响应是否有序
     * @param ctx
     * @param message
     */
    private void checkResponseOrderingRequirements(ChannelHandlerContext ctx, ThriftMessage message) {
        //判定是否需要排序多个响应
        boolean messageRequiresOrderedResponses = message.isOrderedResponsesRequired();

        if (!DispatcherContext.isResponseOrderingRequirementInitialized(ctx)) {
            //第一个请求消息决定了所有的消息是否严格限制顺序或者允许乱序
            DispatcherContext.setResponseOrderingRequired(ctx, messageRequiresOrderedResponses);
        } else {
            // This is not the first request. Verify that the ordering requirement on this message
            // is consistent with the requirement on the channel itself.
            //如果不是第一个请求,验证是不是当前同一个channel发出,并且是否需要排序
            checkState(
                    messageRequiresOrderedResponses == DispatcherContext.isResponseOrderingRequired(ctx),
                    "Every message on a single channel must specify the same requirement for response ordering");
        }
    }

    private void processRequest(
            final ChannelHandlerContext ctx,
            final ThriftMessage message,
            final TSmartTransport messageTransport,
            final TProtocol inProtocol,
            final TProtocol outProtocol) {
        //记录请求到达的顺序,用于回写响应的排序工作
        final int requestSequenceId = dispatcherSequenceId.incrementAndGet();
        //判定如果需要排序,对所有响应进行排序操作,这往往由达到的第一个请求决定.
        if (DispatcherContext.isResponseOrderingRequired(ctx)) {
            //判定条件,如果指定条件满足停止netty自动接受读请求
            synchronized (responseMap) {
                // 通过阻塞读的处理,限制等待响应的数量(响应完成是乱序的,需要等待排序靠前面的请求被完成,从而完成回写响应有序)
                // 为了保证 netty 窗口解码器正常工作,这里更多是一个预估的限制,而不是严格的限制,
                // netty 甚至在channel被堵塞时,依旧可以处理他读取到多个请求,除非没有新的请求送达.
                //接受都的请求id(有序)需要大圩最后一次回写id号+队列限制 (保证负荷)以及 channel没有阻塞
                if (requestSequenceId > lastResponseWrittenId.get() + queuedResponseLimit &&
                        !DispatcherContext.isChannelReadBlocked(ctx)) {
                    //阻塞模式处理
                    DispatcherContext.blockChannelReads(ctx);
                }
            }
        }

        try {
            dispatcherExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    ListenableFuture<Boolean> processFuture;
                    //是否发送响应
                    final AtomicBoolean responseSent = new AtomicBoolean(false);
                    //使用AtomicReference 做为一个持有的类的属性,并把其标识成final,只是为了传递进内部类,
                    // 然而我们只使用get和set方法,并不实际上进行任何原子类操作
                    final AtomicReference<Timeout> expireTimeout = new AtomicReference<Timeout>(null);

                    try {
                        try {
                            long timeRemaining = 0;
                            //消耗时间
                            long timeElapsed = System.currentTimeMillis() - message.getProcessStartTimeMillis();
                            //队列超时抛出异常
                            if (queueTimeoutMillis > 0) {
                                if (timeElapsed >= queueTimeoutMillis) {
                                    TApplicationException taskTimeoutException = new TApplicationException(
                                            TApplicationException.INTERNAL_ERROR,
                                            "Task stayed on the queue for " + timeElapsed +
                                                    " milliseconds, exceeding configured queue timeout of " + queueTimeoutMillis +
                                                    " milliseconds."
                                    );
                                    sendTApplicationException(taskTimeoutException, ctx, message, requestSequenceId, messageTransport,
                                            inProtocol, outProtocol);
                                    return;
                                }
                            }
                            //任务处理超时抛出异常
                            else if (taskTimeoutMillis > 0) {
                                if (timeElapsed >= taskTimeoutMillis) {
                                    TApplicationException taskTimeoutException = new TApplicationException(
                                            TApplicationException.INTERNAL_ERROR,
                                            "Task stayed on the queue for " + timeElapsed +
                                                    " milliseconds, exceeding configured task timeout of " + taskTimeoutMillis +
                                                    " milliseconds."
                                    );
                                    sendTApplicationException(taskTimeoutException, ctx, message, requestSequenceId, messageTransport,
                                            inProtocol, outProtocol);
                                    return;
                                } else {
                                    timeRemaining = taskTimeoutMillis - timeElapsed;
                                }
                            }

                            if (timeRemaining > 0) {
                                //设置到期超时任务,用于创建一个临时的transport 发送异常
                                expireTimeout.set(taskTimeoutTimer.newTimeout(new TimerTask() {
                                    @Override
                                    public void run(Timeout timeout) throws Exception {
                                        //processors 返回的future不能被取消,cancel() 和isCanceled() 总是返回false,
                                        // 使用一个标志侦测任务过期
                                        if (responseSent.compareAndSet(false, true)) {
                                            TApplicationException ex = new TApplicationException(
                                                    TApplicationException.INTERNAL_ERROR,
                                                    "Task timed out while executing."
                                            );
                                            //创建一个临时的transport 发送异常
                                            ByteBuf duplicateBuffer = message.getBuffer().duplicate();
                                            duplicateBuffer.resetReaderIndex();
                                            TSmartTransport temporaryTransport = new TSmartTransport(
                                                    ctx.channel(),
                                                    duplicateBuffer,
                                                    message.getTransportType());
                                            TProtocolPair protocolPair = duplexProtocolFactory.getProtocolPair(
                                                    TTransportPair.fromSingleTransport(temporaryTransport));
                                            sendTApplicationException(ex, ctx, message,
                                                    requestSequenceId,
                                                    temporaryTransport,
                                                    protocolPair.getInputProtocol(),
                                                    protocolPair.getOutputProtocol());
                                        }
                                    }
                                }, timeRemaining, TimeUnit.MILLISECONDS));
                            }
                            //依据channel或得上下文当中的链接上下文当中的链接元数据信息,
                            // 与顶部ConnectionContextHandler保存元数据信息遥相呼应
                            //ConnectionContext connectionContext = ConnectionContexts.getContext(ctx);
                            SmartConnectionContext connectionContext = new SmartConnectionContext();
                            connectionContext.setRemoteAddress(ctx.channel().remoteAddress());
                            //为thrift处理器创建请求信息
                            RequestContext requestContext = new SmartRequestContext(connectionContext, inProtocol, outProtocol, messageTransport);
                            RequestContexts.setCurrentContext(requestContext);
                            //这里所有的thrift操作全部是异步操作,thrift 处理器统一重新包装,由SmartProcessorAdapters 回调原生处理器
                            //原生处理器封装至SmartProcessFactory工厂内
                            processFuture = processorFactory.getProcessor(messageTransport).process(inProtocol, outProtocol, requestContext);
                        } finally {
                            // RequestContext does NOT stay set while we are waiting for the process
                            // future to complete. This is by design because we'll might move on to the
                            // next request using this thread before this one is completed. If you need
                            // the context throughout an asynchronous handler, you need to read and store
                            // it before returning a future.
                            /**
                             * 这里的请求的上线文不会一直等待process的处理完成,这么设计的目的是因为我们在当前请求完成之前使用当前线程继续处理下一个
                             * 请求,如果你需要上下文始终是一个异步处理handler,你需要在他返回future之前读取还有存储他们
                             */
                            RequestContexts.clearCurrentContext();
                        }
                        //为thrift处理添加回调,处理成功后清除过滤器,回写响应
                        Futures.addCallback(
                                processFuture,
                                new FutureCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean result) {
                                        deleteExpirationTimer(expireTimeout.get());
                                        try {
                                            // Only write response if the client is still there and the task timeout
                                            // hasn't expired.
                                            if (ctx.channel().isActive() && responseSent.compareAndSet(false, true)) {
                                                //输出与输入共用messageTransport
                                                ThriftMessage response = message.getMessageFactory()
                                                        .create(messageTransport.getOutputBuffer());
                                                //回写响应
                                                writeResponse(ctx, response, requestSequenceId,
                                                        DispatcherContext.isResponseOrderingRequired(ctx));
                                            }
                                        } catch (Throwable t) {
                                            onDispatchException(ctx, t);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        deleteExpirationTimer(expireTimeout.get());
                                        onDispatchException(ctx, t);
                                    }
                                }
                        );
                    } catch (TException e) {
                        onDispatchException(ctx, e);
                    }
                }
            });
        } catch (RejectedExecutionException ex) {
            TApplicationException x = new TApplicationException(TApplicationException.INTERNAL_ERROR,
                    "Server overloaded");
            sendTApplicationException(x, ctx, message, requestSequenceId, messageTransport, inProtocol, outProtocol);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private void deleteExpirationTimer(Timeout timeout) {
        if (timeout == null) {
            return;
        }
        timeout.cancel();
    }

    /**
     * 发送异常信息
     *
     * @param x
     * @param ctx
     * @param request
     * @param responseSequenceId
     * @param requestTransport
     * @param inProtocol
     * @param outProtocol
     */
    private void sendTApplicationException(
            TApplicationException x,
            ChannelHandlerContext ctx,
            ThriftMessage request,
            int responseSequenceId,
            TSmartTransport requestTransport,
            TProtocol inProtocol,
            TProtocol outProtocol) {
        if (ctx.channel().isActive()) {
            try {
                TMessage message = inProtocol.readMessageBegin();
                outProtocol.writeMessageBegin(new TMessage(message.name, TMessageType.EXCEPTION, message.seqid));
                x.write(outProtocol);
                outProtocol.writeMessageEnd();
                outProtocol.getTransport().flush();

                ThriftMessage response = request.getMessageFactory().create(requestTransport.getOutputBuffer());
                writeResponse(ctx, response, responseSequenceId, DispatcherContext.isResponseOrderingRequired(ctx));
            } catch (TException ex) {
                onDispatchException(ctx, ex);
            }
        }
    }
    //处理分发异常
    private void onDispatchException(ChannelHandlerContext ctx, Throwable t) {
        ctx.fireExceptionCaught(t);
        closeChannel(ctx);
    }

    /**
     * 回写thrift响应,如果需要有序,以顺序模式回写
     * 否则直接回写
     *
     * @param ctx
     * @param response
     * @param responseSequenceId
     * @param isOrderedResponsesRequired
     */
    private void writeResponse(ChannelHandlerContext ctx,
                               ThriftMessage response,
                               int responseSequenceId,
                               boolean isOrderedResponsesRequired) {
        if (isOrderedResponsesRequired) {
            writeResponseInOrder(ctx, response, responseSequenceId);
        } else {
            // No ordering required, just write the response immediately
            ctx.write(response);
            lastResponseWrittenId.incrementAndGet();
        }
    }

    /**
     * 在保证一定顺序的前提下回写响应
     * @param ctx
     * @param response
     * @param responseSequenceId
     */
    private void writeResponseInOrder(ChannelHandlerContext ctx,
                                      ThriftMessage response,
                                      int responseSequenceId) {
        // Ensure responses to requests are written in the same order the requests
        // were received.
        //确保请求对应的响应回写顺序与请求到达的顺序一致
        synchronized (responseMap) {
            int currentResponseId = lastResponseWrittenId.get() + 1;
            if (responseSequenceId != currentResponseId) {
                // This response is NOT next in line of ordered responses, save it to
                // be sent later, after responses to all earlier requests have been
                // sent.
                responseMap.put(responseSequenceId, response);
            } else {
                //循环发送所有响应,知道response为空
                do {
                    ctx.write(response);
                    lastResponseWrittenId.incrementAndGet();
                    ++currentResponseId;
                    response = responseMap.remove(currentResponseId);
                } while (null != response);

                // Now that we've written some responses, check if reads should be unblocked
                //我们已经输出大部分响应,检查一下是否需要放开netty对请求的自动读取
                if (DispatcherContext.isChannelReadBlocked(ctx)) {
                    int lastRequestSequenceId = dispatcherSequenceId.get();
                    //评估一下如果允许,开启netty自动处理read请求
                    if (lastRequestSequenceId <= lastResponseWrittenId.get() + queuedResponseLimit) {
                        DispatcherContext.unblockChannelReads(ctx);
                    }
                }
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
            throws Exception {
        // Any out of band exception are caught here and we tear down the socket
        closeChannel(ctx);

        // Send for logging
        ctx.fireUserEventTriggered(e);
    }

    private void closeChannel(ChannelHandlerContext ctx) {
        if (ctx.channel().isOpen()) {
            ctx.channel().close();
        }
    }
    //链接已经open但是还没有bind 和connect
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // Reads always start out unblocked
        DispatcherContext.unblockChannelReads(ctx);
        super.channelRegistered(ctx);
    }

    private static class DispatcherContext {
        private ReadBlockedState readBlockedState = ReadBlockedState.NOT_BLOCKED; //默认读不阻塞
        private boolean responseOrderingRequired = false; //是否需要对响应排序
        private boolean responseOrderingRequirementInitialized = false; //是否已经初始化

        public static boolean isChannelReadBlocked(ChannelHandlerContext ctx) {
            return getDispatcherContext(ctx).readBlockedState == ReadBlockedState.BLOCKED;
        }

        /**
         * 配置netty禁止自动读取事件
         *
         * @param ctx
         */
        public static void blockChannelReads(ChannelHandlerContext ctx) {
            // Remember that reads are blocked (there is no Channel.getReadable())
            getDispatcherContext(ctx).readBlockedState = ReadBlockedState.BLOCKED;

            /**
             * 注意:这项操作会停止读取,但是不会百分百保证我们不会收到任何消息
             * 这项操作设置channel不会触发新的读事件,也不会从socket当中读取新的消息,但是在设置这项操作之前的已经读取的
             * 数据正常被解码以及被handler处理. 因此在阻塞读之前对于消息队列限制要好于严格的限制
             */
            ctx.channel().config().setAutoRead(false);
        }

        /**
         * 配置netty框架自动读取请求
         *
         * @param ctx
         */
        public static void unblockChannelReads(ChannelHandlerContext ctx) {
            // Remember that reads are unblocked (there is no Channel.getReadable())
            getDispatcherContext(ctx).readBlockedState = ReadBlockedState.NOT_BLOCKED;
            ctx.channel().config().setAutoRead(true);
        }

        public static void setResponseOrderingRequired(ChannelHandlerContext ctx, boolean required) {
            DispatcherContext dispatcherContext = getDispatcherContext(ctx);
            dispatcherContext.responseOrderingRequirementInitialized = true;
            dispatcherContext.responseOrderingRequired = required;
        }

        public static boolean isResponseOrderingRequired(ChannelHandlerContext ctx) {
            return getDispatcherContext(ctx).responseOrderingRequired;
        }

        /**
         * 从DispatcherContext当中或得是否为当前上线文进行有序保证
         *
         * @param ctx
         * @return
         */
        public static boolean isResponseOrderingRequirementInitialized(ChannelHandlerContext ctx) {
            return getDispatcherContext(ctx).responseOrderingRequirementInitialized;
        }

        private static DispatcherContext getDispatcherContext(ChannelHandlerContext ctx) {
            AttributeKey<DispatcherContext> dispatcherKey = AttributeKey.valueOf(DispatcherContext.class.getSimpleName());
            DispatcherContext dispatcherContext = ctx.attr(dispatcherKey).get();
            if (dispatcherContext==null) {
                // No context was added yet, add one
                dispatcherContext = new DispatcherContext();
                ctx.attr(dispatcherKey).set(dispatcherContext);
                return  dispatcherContext;
            }
            else {
                return dispatcherContext;
            }

        }

        private enum ReadBlockedState {
            NOT_BLOCKED,
            BLOCKED,
        }
    }
}
