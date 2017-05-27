package com.si.jupiter.smart.pools;

import java.io.UnsupportedEncodingException;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-27 下午2:42
 * Last Update:  17-5-27 下午2:42
 */
public class SafeEncoder {
    public SafeEncoder() {
    }

    public static byte[][] encodeMany(String... strs) {
        byte[][] many = new byte[strs.length][];

        for(int i = 0; i < strs.length; ++i) {
            many[i] = encode(strs[i]);
        }

        return many;
    }

    public static byte[] encode(String str) {
        try {
            if(str == null) {
                throw new SafeEncodeException("value sent to redis cannot be null");
            } else {
                return str.getBytes("UTF-8");
            }
        } catch (UnsupportedEncodingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static String encode(byte[] data) {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new SafeEncodeException(var2);
        }
    }
    public static class SafeEncodeException extends RuntimeException{
        public SafeEncodeException(String message){
            super(message);
        }
        public SafeEncodeException(Throwable cause){
            super(cause);
        }
    }
}
