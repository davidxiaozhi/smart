package com.si.jupiter.smart.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-9 下午4:37
 * Last Update:  17-6-9 下午4:37
 */
public class ThriftSerializable <T> implements RpcSerializable<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftSerializable.class);
    private final static RpcSerializable SERIALIZABLE = new ThriftSerializable();
    @Override
    public T decode(byte[] value, Class<T> clazz) {
        return null;
    }

    @Override
    public byte[] encode(T value) {
        return new byte[0];
    }
}
