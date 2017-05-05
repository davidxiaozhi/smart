package com.si.jupiter.smart.network;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 12:12
 */
public interface RpcSerializable<T> {
    T decode(byte[] value, Class<T> clazz);

    byte[] encode(T value);
}
