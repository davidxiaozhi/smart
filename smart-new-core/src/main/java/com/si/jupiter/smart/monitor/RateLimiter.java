package com.si.jupiter.smart.monitor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: lizhipeng
 * Date: 2017/04/11 10:23
 * 限流模块
 */
public class RateLimiter {
    private final ConcurrentHashMap<String, AtomicInteger> serviceMonitorMap = new ConcurrentHashMap<>();//服务监控
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> methodMonitorMap = new ConcurrentHashMap<>();//方法监控

    /**
     * 获取服务当前并发流量
     *
     * @param service 服务名称
     * @return 并发计算
     */
    public int getServiceCount(String service) {
        if (service == null) {
            throw new IllegalArgumentException("arg service is null.");
        }
        AtomicInteger counter = this.serviceMonitorMap.get(service);
        if (counter != null) {
            counter.get();
        }
        return 0;
    }

    /**
     * 获取某个方法的当前并发流量
     *
     * @param service 服务名称
     * @param method  方法名称
     * @return 并发计数
     */
    public int getMethodCount(String service, String method) {
        if (service == null || method == null) {
            throw new IllegalArgumentException("arg service or method is null.");
        }
        ConcurrentHashMap<String, AtomicInteger> methodCount = this.methodMonitorMap.get(service);
        if (methodCount != null) {
            AtomicInteger counter = methodCount.get(method);
            return counter == null ? 0 : counter.get();
        }
        return 0;
    }

    /**
     * 给服务和方法计数加1
     *
     * @param service 服务名称
     * @param method  方法名称
     */
    public void increment(String service, String method) {
        if (service == null || method == null) {
            throw new IllegalArgumentException("arg service or method is null.");
        }
        AtomicInteger serviceCounter = serviceMonitorMap.get(service);
        if (serviceCounter == null) {
            serviceCounter = new AtomicInteger(0);
            AtomicInteger serviceCounterTmp = serviceMonitorMap.putIfAbsent(service, serviceCounter);
            if (serviceCounterTmp != null) {
                serviceCounter = serviceCounterTmp;
            }
        }
        serviceCounter.incrementAndGet();
        ConcurrentHashMap<String, AtomicInteger> methodMap = methodMonitorMap.get(service);
        if (methodMap == null) {
            methodMap = new ConcurrentHashMap<>();
            methodMap.put(method, new AtomicInteger(0));
            ConcurrentHashMap<String, AtomicInteger> methodMamTmp = methodMonitorMap.putIfAbsent(service, methodMap);
            if (methodMamTmp != null) {
                methodMap = methodMamTmp;
            }
        }
        AtomicInteger methodCounter = methodMap.get(method);
        methodCounter.incrementAndGet();
    }

    /**
     * 给服务和方法计数减1
     *
     * @param service 服务名称
     * @param method  方法名称
     */
    public void decrement(String service, String method) {
        if (service == null || method == null) {
            throw new IllegalArgumentException("arg service or method is null.");
        }
        AtomicInteger serviceCounter = serviceMonitorMap.get(service);
        if (serviceCounter != null) {
            int serviceCount = serviceCounter.decrementAndGet();
            if (serviceCount < 0) {
                serviceCounter.set(0);
            }
        }
        ConcurrentHashMap<String, AtomicInteger> methodMap = methodMonitorMap.get(service);
        if (methodMap != null) {
            AtomicInteger methodCounter = methodMap.get(method);
            if (methodCounter != null) {
                int methodCount = methodCounter.decrementAndGet();
                if (methodCount < 0) {
                    methodCounter.set(0);
                }
            }
        }
    }
}
