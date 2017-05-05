package com.si.jupiter.smart.network;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeEnv;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: lizhipeng
 * Date: 2017/01/05 13:05
 */
public class ProtobufSerializable<T> implements RpcSerializable<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProtobufSerializable.class);
    private static Map<Class<?>, Schema<?>> cacheSchama = new ConcurrentHashMap<>(64);
    private static Objenesis objenesis = new ObjenesisStd(true);
    private final static RpcSerializable SERIALIZABLE = new ProtobufSerializable();

    private ProtobufSerializable() {
    }

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cacheSchama.get(cls);
        if (null == schema) {
            synchronized (ProtobufSerializable.class) {
                if (null == cacheSchama.get(cls)) {
                    schema = RuntimeSchema.createFrom(cls, new HashSet<String>(16), RuntimeEnv.ID_STRATEGY);
                    cacheSchama.put(cls, schema);
                }
            }
        }
        return schema;
    }

    public static RpcSerializable newInstance() {
        return SERIALIZABLE;
    }

    /**
     * 序列化
     *
     * @param obj 对象
     * @return 二进制对象
     */
    public byte[] encode(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception ex) {
            LOGGER.error("Serialize error", ex);
        } finally {
            buffer.clear();
        }

        return null;
    }

    /**
     * 反序列化
     *
     * @param data 二进制值
     * @param cls  类
     * @return 对象
     */
    public T decode(byte[] data, Class<T> cls) {
        try {
            T obj = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return obj;
        } catch (Exception ex) {
            LOGGER.error("Deserialize error", ex);

        }
        return null;
    }
}
