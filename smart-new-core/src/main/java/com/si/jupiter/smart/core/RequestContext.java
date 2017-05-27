package com.si.jupiter.smart.core;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-16 上午11:54
 * Last Update:  17-5-16 上午11:54
 */
public class RequestContext {
    public static  final  String EMPTY = "r-empty";
    private RequestContext(){}
    private static final ThreadLocal<RequestContext> LOCAL = new ThreadLocal<RequestContext>() {
        @Override
        protected RequestContext initialValue() {
            RequestContext requestContext =new RequestContext();
            return requestContext;
        }
    };
    private String dispachKey = EMPTY;

    public static RequestContext getContext() {
        return LOCAL.get();
    }

    public String getDispachKey() {
        return dispachKey;
    }

    public void setDispachKey(String dispachKey) {
        this.dispachKey = dispachKey;
    }
}
