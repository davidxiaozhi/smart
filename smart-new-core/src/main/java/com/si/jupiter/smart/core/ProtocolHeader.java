package com.si.jupiter.smart.core;

import java.io.Serializable;
import java.nio.charset.Charset;

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

    public byte[] getHeaderBytes(){
        if(this.getToken()!=null&&!"".equals(this.getToken().trim())){
            return this.getToken().getBytes(Charset.forName("UTF-8"));
        }
        return new byte[0];
    }

    public void setHeaderBytes(byte[] headBytes){
        if(headBytes!=null&&headBytes.length!=0){
            this.token = new String(headBytes,Charset.forName("UTF-8"));
        }
    }

    public void setToken(String token) {
        this.token = token;
    }
}
