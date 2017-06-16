package com.si.jupiter.smart.server;

import com.si.jupiter.smart.core.*;
import com.si.jupiter.smart.network.SerializableHandler;
import com.si.jupiter.smart.server.codec.ServerProtocolProcesser;
import com.si.jupiter.smart.server.codec.ServiceUtils;
import com.si.jupiter.smart.server.codec.SProtocolProcesser;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 09:40
 * 服务处理类
 */
public class ServiceInvokeManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceInvokeManager.class);
    private SProtocolProcesser protocolProcesser;
    private ThreadPoolExecutor executor;
    private ServerConfig config;

    /**
     * 构造函数
     *
     * @param config   配置
     * @param executor 执行线程池
     */
    public ServiceInvokeManager(ServerConfig config, ThreadPoolExecutor executor) {
        this.executor = executor;
        this.config = config;
        protocolProcesser = new ServerProtocolProcesser();
    }

    /**
     * 在业务线程池当中执行服务接口调用
     *
     * @param protocol 网络协议对象
     * @param ctx      channel对象
     */
    public void invoke(final NetworkProtocol protocol, final ChannelHandlerContext ctx) {
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                //将协议当中context进行反序列化
                RpcInvocation invocation = SerializableHandler.requestDecode(protocol);
                long reqTime = protocol.getRequestTime();
                int timeout = protocol.getRequestTimeout();
                if (System.currentTimeMillis() - reqTime < timeout) {//超时的任务就不用执行了
                    String methodName = ServiceUtils.buildMethodName(invocation.getMethod(), invocation.getArgTypes());
                    Object res = null;
                    Throwable throwable = null;
                    try {
                        res = invoke0(invocation.getService(), invocation.getVersion(), methodName, invocation.getArgs());
                    } catch (Exception e) {
                        throwable = e;
                        LOGGER.error("Invoke exception.", e);
                    }
                    if (System.currentTimeMillis() - reqTime < timeout) {//超时的任务就不用返回了
                        try {
                            RpcResult result = buildRpcResult(200, throwable, res);
                            NetworkProtocol responseProtocol = protocolProcesser.buildResponseProtocol(protocol, result);
                            ChannelFuture channelFuture = ctx.writeAndFlush(responseProtocol);
                            if (LOGGER.isDebugEnabled()) {
                                final long startTime = System.currentTimeMillis();
                                channelFuture.addListeners(new ChannelFutureListener() {
                                    @Override
                                    public void operationComplete(ChannelFuture future) throws Exception {
                                        LOGGER.debug("smart send msg packageId={} cost {}ms, exception={}", protocol.getSequence(), (System.currentTimeMillis() - startTime), future.cause());
                                    }
                                });
                            }
                        } catch (Exception e) {
                            LOGGER.error("Server invoke fail.", e);
                        }
                    }
                }
            }
        });
    }

    /**
     * 执行方法
     *
     * @param method 方法对象
     * @param args   方法参数
     * @return Object
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private Object invoke0(String serviceName, String version, String method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        Object service = ServiceMapper.getSerivce(serviceName, version);
        Method m = ServiceMapper.getMethod(serviceName,version, method);
        if (m != null) {
            return m.invoke(service, args);
        }
        throw new IllegalAccessException("No method: " + m.getName() + " find on the server");
    }


    private RpcResult buildRpcResult(int status, Throwable throwable, Object res) {
        RpcResult rpcResult = new RpcResult();
        rpcResult.setException(throwable);
        rpcResult.setStatus(status);
        rpcResult.setValue(res);
        return rpcResult;
    }
}
