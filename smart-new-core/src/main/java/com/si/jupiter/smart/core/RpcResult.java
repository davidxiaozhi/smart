package com.si.jupiter.smart.core;

import java.io.Serializable;
import java.util.Map;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 13:09
 * 服务端返回结果封装
 */
public class RpcResult implements Serializable {
    private static final long serialVersionUID = -5618624997366006373L;
    private int status;//结果状态
    private Object value;//结果对象
    private Throwable exception;//服务端异常信息
    private Map<String, Object> attach;//附加属性

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Map<String, Object> getAttach() {
        return attach;
    }

    public void setAttach(Map<String, Object> attach) {
        this.attach = attach;
    }

    @Override
    public String toString() {
        return "RpcResult{" +
                "status=" + status +
                ", value=" + value +
                ", exception=" + exception +
                ", attach=" + attach +
                '}';
    }
}
