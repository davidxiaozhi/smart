/*
 * Copyright (C) 2012-2013 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.si.jupiter.smart.client.channel;


import com.si.jupiter.smart.client.transport.TChannelBufferInputTransport;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.si.jupiter.smart.utils.Duration;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 双向handle用来发送接收消息
 */
public abstract class AbstractClientChannel extends ChannelDuplexHandler implements
        SmartClientChannel {
    private static final Logger LOGGER = Logger.getLogger(AbstractClientChannel.class.getCanonicalName());

    private final Channel nettyChannel;
    private Duration sendTimeout = null;

    // Timeout until the whole request must be received.
    private Duration receiveTimeout = null;

    // Timeout for not receiving any data from the server
    private Duration readTimeout = null;

    private final Map<Integer, Request> requestMap = new HashMap<Integer, Request>();
    private volatile TException channelError;
    private final Timer timer;
    private final TDuplexProtocolFactory protocolFactory;

    protected AbstractClientChannel(Channel nettyChannel, Timer timer, TDuplexProtocolFactory protocolFactory) {
        this.nettyChannel = nettyChannel;
        this.timer = timer;
        this.protocolFactory = protocolFactory;
    }

    @Override
    public Channel getNettyChannel() {
        return nettyChannel;
    }

    @Override
    public TDuplexProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    protected abstract ByteBuf extractResponse(Object message) throws TTransportException;

    /**
     * 读取thrift请求响应的序号Id
     * @param messageBuffer
     * @return
     * @throws TTransportException
     */
    protected int extractSequenceId(ByteBuf messageBuffer)
            throws TTransportException {
        try {
            messageBuffer.markReaderIndex();
            TTransport inputTransport = new TChannelBufferInputTransport(messageBuffer);
            TProtocol inputProtocol = getProtocolFactory().getInputProtocolFactory().getProtocol(inputTransport);
            TMessage message = inputProtocol.readMessageBegin();
            messageBuffer.resetReaderIndex();
            return message.seqid;
        } catch (Throwable t) {
            throw new TTransportException("Could not find sequenceId in Thrift message", t);
        }
    }

    protected abstract ChannelFuture writeRequest(ByteBuf request);

    @Override
    public void setSendTimeout(Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }

    @Override
    public Duration getSendTimeout() {
        return sendTimeout;
    }

    @Override
    public void setReceiveTimeout(Duration receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    @Override
    public Duration getReceiveTimeout() {
        return receiveTimeout;
    }

    @Override
    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public Duration getReadTimeout() {
        return this.readTimeout;
    }

    @Override
    public boolean hasError() {
        return channelError != null;
    }

    @Override
    public TException getError() {
        return channelError;
    }

    @Override
    public void executeInIoThread(Runnable runnable) {
        NioSocketChannel nioSocketChannel = (NioSocketChannel) getNettyChannel();
        //nioSocketChannel.getWorker().executeInIoThread(runnable, true);
    }

    @Override
    public void sendAsynchronousRequest(final ByteBuf message,
                                        final boolean oneway,
                                        final Listener listener)
            throws TException {
        final int sequenceId = extractSequenceId(message);

        // Ensure channel listeners are always called on the channel's I/O thread
        executeInIoThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Request request = makeRequest(sequenceId, listener);

                    if (!nettyChannel.isActive()) {
                        fireChannelErrorCallback(listener, new TTransportException(TTransportException.NOT_OPEN, "Channel closed"));
                        return;
                    }

                    if (hasError()) {
                        fireChannelErrorCallback(
                                listener,
                                new TTransportException(TTransportException.UNKNOWN, "Channel is in a bad state due to failing a previous request"));
                        return;
                    }

                    ChannelFuture sendFuture = writeRequest(message);
                    //queueSendTimeout(request);

                    sendFuture.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            messageSent(future, request, oneway);
                        }
                    });
                } catch (Throwable t) {
                    // onError calls all registered listeners in the requestMap, but this request
                    // may not be registered yet. So we try to remove it (to make sure we don't call
                    // the callback twice) and then manually make the callback for this request
                    // listener.
                    requestMap.remove(sequenceId);
                    fireChannelErrorCallback(listener, t);

                    onError(t);
                }
            }
        });
    }

    private void messageSent(ChannelFuture future, Request request, boolean oneway) {
        try {
            if (future.isSuccess()) {
                cancelRequestTimeouts(request);
                fireRequestSentCallback(request.getListener());
                retireRequest(request);
            } else {
                TTransportException transportException =
                        new TTransportException("Sending request failed",
                                future.cause());
                onError(transportException);
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    /**
     * 处理服务端发送回来的信息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf response = extractResponse(msg);

            if (response != null) {
                int sequenceId = extractSequenceId(response);
                onResponseReceived(sequenceId, response);
            } else {
                ctx.fireChannelRead(msg);
            }
        } catch (Throwable t) {
            onError(t);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Throwable t = cause.getCause();
        onError(t);
    }

    private Request makeRequest(int sequenceId, Listener listener) {
        Request request = new Request(listener);
        requestMap.put(sequenceId, request);
        return request;
    }

    private void retireRequest(Request request) {
        cancelRequestTimeouts(request);
    }

    private void cancelRequestTimeouts(Request request) {
        Timeout sendTimeout = request.getSendTimeout();
        if (sendTimeout != null && !sendTimeout.isCancelled()) {
            sendTimeout.cancel();
        }

        Timeout receiveTimeout = request.getReceiveTimeout();
        if (receiveTimeout != null && !receiveTimeout.isCancelled()) {
            receiveTimeout.cancel();
        }

        Timeout readTimeout = request.getReadTimeout();
        if (readTimeout != null && !readTimeout.isCancelled()) {
            readTimeout.cancel();
        }
    }

    private void cancelAllTimeouts() {
        for (Request request : requestMap.values()) {
            cancelRequestTimeouts(request);
        }
    }

    private void onResponseReceived(int sequenceId, ByteBuf response) {
        Request request = requestMap.remove(sequenceId);
        if (request == null) {
            onError(new TTransportException("Bad sequence id in response: " + sequenceId));
        } else {
            retireRequest(request);
            fireResponseReceivedCallback(request.getListener(), response);
        }
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        if (!requestMap.isEmpty()) {
            onError(new TTransportException("Client was disconnected by server"));
        }
    }

    protected void onError(Throwable t) {
        TException wrappedException = wrapException(t);

        if (channelError == null) {
            channelError = wrappedException;
        }

        cancelAllTimeouts();

        Collection<Request> requests = new ArrayList<Request>();
        requests.addAll(requestMap.values());
        requestMap.clear();
        for (Request request : requests) {
            fireChannelErrorCallback(request.getListener(), wrappedException);
        }

        Channel channel = getNettyChannel();
        if (nettyChannel.isOpen()) {
            channel.close();
        }
    }

    protected TException wrapException(Throwable t) {
        if (t instanceof TException) {
            return (TException) t;
        } else {
            return new TTransportException(t);
        }
    }

    private void fireRequestSentCallback(Listener listener) {
        try {
            listener.onRequestSent();
        } catch (Throwable t) {
            System.out.println("Request sent listener callback triggered an exception");
        }
    }

    private void fireResponseReceivedCallback(Listener listener, ByteBuf response) {
        try {
            listener.onResponseReceived(response);
        } catch (Throwable t) {
            System.out.println("Response received listener callback triggered an exception");
        }
    }

    private void fireChannelErrorCallback(Listener listener, TException exception) {
        try {
            listener.onChannelError(exception);
        } catch (Throwable t) {
            System.out.println("Channel error listener callback triggered an exception");
        }
    }

    private void fireChannelErrorCallback(Listener listener, Throwable throwable) {
        fireChannelErrorCallback(listener, wrapException(throwable));
    }

    /**
     * Bundles the details of a client request that has started, but for which a response hasn't
     * yet been received (or in the one-way case, the send operation hasn't completed yet).
     */
    private static class Request {
        private final Listener listener;
        private Timeout sendTimeout;
        private Timeout receiveTimeout;

        private volatile Timeout readTimeout;

        public Request(Listener listener) {
            this.listener = listener;
        }

        public Listener getListener() {
            return listener;
        }

        public Timeout getReceiveTimeout() {
            return receiveTimeout;
        }

        public void setReceiveTimeout(Timeout receiveTimeout) {
            this.receiveTimeout = receiveTimeout;
        }

        public Timeout getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(Timeout readTimeout) {
            this.readTimeout = readTimeout;
        }

        public Timeout getSendTimeout() {
            return sendTimeout;
        }

        public void setSendTimeout(Timeout sendTimeout) {
            this.sendTimeout = sendTimeout;
        }
    }
}
