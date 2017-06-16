package com.si.jupiter.smart.core.thrift;

import org.apache.thrift.TBaseProcessor;

/**
 * todo       : thrift相关配置类
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-12 下午5:31
 * Last Update:  17-6-12 下午5:31
 */
public class ServerThriftConfig<T>{
    private final Class<T> interfaze;//服务接口类
    private final Class<? extends TBaseProcessor<T>> tProcessor;
    private final T iface;
    private final ProtocolType inputProtocol;
    private final ProtocolType outputProtocol;
    public ServerThriftConfig(Class<T> interfaze, Class<? extends TBaseProcessor<T>> tProcessor,T iface, ProtocolType tProtocol) {
        this.interfaze = interfaze;
        this.tProcessor = tProcessor;
        this.iface = iface;
        this.inputProtocol = tProtocol;
        this.outputProtocol = tProtocol;
    }
    public ServerThriftConfig(Class<T> interfaze, Class<? extends TBaseProcessor<T>> tProcessor,T iface, ProtocolType inputProtocol, ProtocolType outputProtocol) {
        this.interfaze = interfaze;
        this.tProcessor = tProcessor;
        this.iface = iface;
        this.inputProtocol = inputProtocol;
        this.outputProtocol = outputProtocol;
    }

    public Class<T> getInterfaze() {
        return interfaze;
    }

    public Class<? extends TBaseProcessor<T>> gettProcessor() {
        return tProcessor;
    }

    public T getIface() {
        return iface;
    }

    public ProtocolType getInputProtocol() {
        return inputProtocol;
    }

    public ProtocolType getOutputProtocol() {
        return outputProtocol;
    }
}
