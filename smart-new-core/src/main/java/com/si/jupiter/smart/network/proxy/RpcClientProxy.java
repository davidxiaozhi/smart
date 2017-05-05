package com.si.jupiter.smart.network.proxy;

import com.si.jupiter.smart.clent.ClientManager;
import com.si.jupiter.smart.core.RpcContext;
import com.si.jupiter.smart.core.RpcFuture;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 17:29
 */
public class RpcClientProxy<T> implements MethodInterceptor {
    private final Enhancer enhancer = new Enhancer();
    protected ClientManager manager;
    private String serviceName;

    public RpcClientProxy(ClientManager manager) {
        this.manager = manager;
    }

    /**
     * 获取代理类
     *
     * @param interfaceClass 客户端接口对象
     * @return
     */
    public T getProxy(Class<T> interfaceClass) {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(this);
        serviceName = interfaceClass.getName();
        return (T) enhancer.create();
    }

    public T intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Exception {
        String methodName = method.getName();
        if (args == null || args.length == 0) {
            if ("toString".equals(methodName) || "hashCode".equals(methodName) || "finalize".equals(method.getName())) {
                try {
                    return (T) methodProxy.invokeSuper(obj, args);
                } catch (Throwable throwable) {
                }
            }
        } else if (args.length == 1 && "equals".equals(methodName)) {
            try {
                return (T) methodProxy.invokeSuper(obj, args);
            } catch (Throwable throwable) {
            }
        }
        return this.invoke(method, args);
    }

    public T invoke(Method method, Object[] args) throws Exception {
        RpcContext context = RpcContext.getContext();
        if (context.isCallbackInvoke()) {
            manager.asyncCallbackInvoke(serviceName, method, args, context.getRpcCallback());
        } else if (context.isFutureInvoke()) {
            RpcFuture<T> resultFuture = manager.asyncFutureInvoke(serviceName, method, args);
            context.getFuture().copyFuture(resultFuture);
        }
        return (T) manager.invoke(serviceName, method, args);
    }
}