package com.si.jupiter.smart.route;


/**
 * Author: lizhipeng
 * Date: 2017/01/09 10:56
 */
public enum RouteEnum {
    ROUND("round"),CONSISTENTHASH("consistentHash");
    private String route;
    private RpcRoute rpcRoute;

    RouteEnum(String route) {
        this.route = route;
    }

    public String getValue() {
        return this.route;
    }

}