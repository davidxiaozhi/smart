package com.si.jupiter.smart.core;

import java.io.Serializable;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:20
 * todo: 存放协议的头信息,这里主要用作包含权限信息用作权限验证
 */
public class ProtocolHeader implements Serializable {
    private String token="/smart/token:112345";

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
