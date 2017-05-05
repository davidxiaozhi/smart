package com.si.jupiter.smart.core;

import java.util.concurrent.Future;

/**
 * Author: lizhipeng
 * Date: 2017/01/20 10:46
 */
public abstract class RpcFuture<T> implements Future<T> {
    protected long sendTime = 0;//发送的时间
    protected int invokerTimeout = 0;//调用超时

    public RpcFuture(long sendTime, int invokerTimeout) {
        this.sendTime = sendTime;
        this.invokerTimeout = invokerTimeout;
    }

    /**
     * 服务端响应消息后的回调
     *
     * @param response 响应数据
     */
    public abstract void responseReceived(T response);

    /**
     * 复制一个future
     *
     * @param future future对象
     */
    public abstract void copyFuture(RpcFuture<T> future);

    public long getSendTime() {
        return sendTime;
    }

    public int getInvokerTimeout() {
        return invokerTimeout;
    }
}
