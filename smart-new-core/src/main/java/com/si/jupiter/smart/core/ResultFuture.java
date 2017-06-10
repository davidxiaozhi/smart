package com.si.jupiter.smart.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ResponseFuture　子类主要负责请求　响应关系的维护
 * Author: lizhipeng
 * Date: 2017/01/20 12:00
 */
public class ResultFuture<T> extends ResponseFuture<T> {
    private volatile RpcFuture<RpcResult> future;
    private int packageId;//请求序列号

    public ResultFuture(RpcFuture<RpcResult> future, int packageId) {
        super(future.invokerTimeout);
        this.future = future;
        this.packageId = packageId;
    }

    public ResultFuture(int invokerTimeout) {
        super(invokerTimeout);
    }

    public ResultFuture() {
        this(0);
    }

    @Override
    public void copyFuture(RpcFuture<T> future) {
        if (future == null) {
            throw new NullPointerException("Future is null");
        }
        if (future instanceof ResultFuture) {
            ResultFuture r = (ResultFuture) future;
            this.future = r.future;
            this.packageId = r.packageId;
        } else {
            throw new IllegalArgumentException("Future type error." + future.getClass().getSimpleName());
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        RpcResult result = null;
        try {
            result = future.get();
        } finally {
            if (result == null) {//客户端超时
                FuturesManager.remove(packageId);
            }
        }
        if (result != null) {
            Throwable exception = result.getException();
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return (T) result.getValue();
        }
        return null;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        RpcResult result = null;
        try {
            result = future.get(timeout, unit);
        } finally {
            if (result == null) {//客户端超时
                FuturesManager.remove(packageId);
            }
        }
        if (result != null) {
            Throwable exception = result.getException();
            if (exception != null) {
                throw new ExecutionException(exception);
            }
            return (T) result.getValue();
        }
        return null;
    }
}
