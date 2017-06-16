package com.si.jupiter.smart.core.thrift;

import com.si.jupiter.smart.server.ServerConfig;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-15 下午3:35
 * Last Update:  17-6-15 下午3:35
 */
public class ThriftProcesser {
    private final ServerConfig config;

    public ThriftProcesser(ServerConfig config) {
        this.config = config;
    }

    /**
     * 序列化原生的thrift请求
     * @return
     */
    public <T> byte[] process(byte[] bytes) throws TException {
        ByteArrayInputStream  byteArrayInputStream = new ByteArrayInputStream(bytes);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        ServerThriftConfig<T> thriftConfig = config.getThriftConfig();
        T iface = thriftConfig.getIface();
        try {
            Constructor<? extends TBaseProcessor<T>> constructor = thriftConfig.gettProcessor().getConstructor(thriftConfig.getInterfaze());
            TProtocol inputProtocol = ThriftUtils.getProtocolByType(thriftConfig.getInputProtocol(),byteArrayInputStream);
            TProtocol outputProtocol = ThriftUtils.getProtocolByType(thriftConfig.getOutputProtocol(),byteArrayOutputStream);
            TBaseProcessor<T> tBaseProcessor = constructor.newInstance(iface);
            tBaseProcessor.process(inputProtocol,outputProtocol);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

}
