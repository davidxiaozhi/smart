package com.si.jupiter.smart.server.codec;

import java.lang.reflect.Method;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-15 下午2:22
 * Last Update:  17-6-15 下午2:22
 */
public class ServiceUtils {
    /**
     * 根据方法对象生成方法签名
     *
     * @param method 方法对象
     * @return String
     */
    public static String buildMethodName(Method method) {
        Class[] pram = method.getParameterTypes();
        StringBuilder builder = new StringBuilder(method.getName());
        if (pram != null) {
            for (Class aClass : pram) {
                builder.append(":").append(aClass.getSimpleName());
            }
        }
        return builder.toString();
    }

    /**
     * 根据方法名称和参数类型生成方法签名
     *
     * @param method 方法名
     * @param args   方法参数
     * @return String
     */
    public static String buildMethodName(String method, String[] args) {
        StringBuilder builder = new StringBuilder(method);
        if (args != null) {
            for (String type : args) {
                builder.append(":").append(type);
            }
        }
        return builder.toString();
    }
}
