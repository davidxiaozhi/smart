package com.si.jupiter.smart.core;

import java.lang.reflect.Method;

public class SmartRespones {
    private final String serviceName;
    private final String version;
    private final Method method;
    private final Object[] args;
    private final int seq;

    /**
     * @param serviceName 服务名称
     * @param version     服务版本
     * @param method      方法
     * @param args        参数
     */
    public SmartRespones(String serviceName, String version, Method method, Object[] args, int seq) {
        this.serviceName = serviceName;
        this.version = version;
        this.method = method;
        this.args = args;
        this.seq = seq;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public int getSeq() {
        return seq;
    }
}
