package com.si.jupiter.smart.core;

import java.io.Serializable;
import java.util.Map;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 11:55
 * RPC客户端调用封装类
 */
public class RpcInvocation implements Serializable {
    private static final long serialVersionUID = -5618624997366006383L;
    private String service;//服务名称
    private String version;//服务版本
    private String method;//方法名称
    private Object[] args;//方法参数
    private String[] argTypes;//方法参数类型
    private Map<String, Object> attach;//附加属性

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String[] getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(String[] argTypes) {
        this.argTypes = argTypes;
    }

    public Map<String, Object> getAttach() {
        return attach;
    }

    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
