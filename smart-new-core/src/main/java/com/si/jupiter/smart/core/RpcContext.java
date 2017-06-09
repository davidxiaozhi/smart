package com.si.jupiter.smart.core;

import java.util.concurrent.Future;

/**
 * Author: lizhipeng
 * Date: 2017/01/20 14:28
 */
public class RpcContext {
    private boolean isFutureInvoke = false;
    private boolean isCallbackInvoke = false;
    private RpcCallback rpcCallback;
    private RpcFuture future;

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>() {
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    public static RpcContext getContext() {
        return LOCAL.get();
    }

    public static void invokeWithCallback(AsyncPrepare prepare, RpcCallback rpcCallback) {
        if (null == prepare) {
            throw new NullPointerException("prepare");
        }
        if (null == rpcCallback) {
            throw new NullPointerException("rpcCallback");
        }
        RpcContext rpcContext = LOCAL.get();
        try {
            rpcContext.isCallbackInvoke = true;
            rpcContext.rpcCallback = rpcCallback;
            prepare.prepare();
        } finally {
            rpcContext.clearAsyncContext();
        }
    }

    public static <T> Future<T> invokeWithFuture(AsyncPrepare prepare) {
        if (null == prepare) {
            throw new NullPointerException("prepare");
        }
        RpcContext rpcContext = LOCAL.get();
        try {
            rpcContext.isFutureInvoke = true;
            rpcContext.future = new ResultFuture<T>();
            prepare.prepare();
            return rpcContext.future;
        } finally {
            rpcContext.clearAsyncContext();
        }
    }

    private void clearAsyncContext() {
        this.isCallbackInvoke = false;
        this.isFutureInvoke = false;
        this.rpcCallback = null;
        this.future = null;
    }

    public boolean isFutureInvoke() {
        return isFutureInvoke;
    }

    public boolean isCallbackInvoke() {
        return isCallbackInvoke;
    }

    public RpcCallback getRpcCallback() {
        return rpcCallback;
    }

    public RpcFuture getFuture() {
        return future;
    }
}
