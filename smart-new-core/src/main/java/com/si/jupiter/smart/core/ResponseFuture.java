package com.si.jupiter.smart.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Author: lizhipeng
 * Date: 2017/01/06 19:24
 * 结果封装Future
 */
public class ResponseFuture<T> extends RpcFuture<T> {
    private Semaphore lock = new Semaphore(0);
    private T response;
    private volatile boolean done = false;

    public ResponseFuture(int invokerTimeout) {
        super(System.currentTimeMillis(), invokerTimeout);
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public boolean isDone() {
        return this.done;
    }

    public T get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            lock.acquire();
        }
        return this.response;
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!isDone()) {
            boolean done = lock.tryAcquire(timeout, unit);
            if (!done) {
                throw new TimeoutException("Waiting response timeout! timeout="+timeout+"ms");
            }
        }
        return this.response;
    }

    public void copyFuture(RpcFuture<T> future) {

    }

    /**
     * 设置结果
     *
     * @param response 服务返回结果
     */
    public void responseReceived(T response) {
        this.response = response;
        this.done = true;
        this.lock.release();
    }
}
