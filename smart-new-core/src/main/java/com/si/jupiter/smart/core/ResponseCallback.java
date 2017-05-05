package com.si.jupiter.smart.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Author: lizhipeng
 * Date: 2017/01/20 10:50
 * 回调函数，注意需要定时清理服务端没有响应的请求
 */
public class ResponseCallback extends RpcFuture<RpcResult> {
    private RpcCallback callback;
    private boolean done = false;

    public ResponseCallback(RpcCallback callback, int invokerTimeout) {
        super(System.currentTimeMillis(), invokerTimeout);
        this.callback = callback;
    }

    public ResponseCallback(int invokerTimeout) {
        super(System.currentTimeMillis(), invokerTimeout);
    }

    @Override
    public void responseReceived(RpcResult response) {
        this.done = true;
        Throwable exception = response.getException();
        if (exception != null) {
            callback.fail(exception);
        } else {
            callback.success(response.getValue());
        }
    }

    @Override
    public void copyFuture(RpcFuture<RpcResult> future) {

    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public RpcResult get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public RpcResult get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
