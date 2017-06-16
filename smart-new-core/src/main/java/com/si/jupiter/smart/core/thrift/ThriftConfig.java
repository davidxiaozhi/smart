package com.si.jupiter.smart.core.thrift;

/**
 * todo       : thrift相关配置类
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-12 下午5:31
 * Last Update:  17-6-12 下午5:31
 */
public class ThriftConfig<T>{
    private final Class<T> interfaze;//服务接口类
    private final Class<? extends T> client;
    private final ProtocolType inputProtocol;
    private final ProtocolType outputProtocol;
    public ThriftConfig(Class<T> interfaze, Class<? extends T> client, ProtocolType tProtocol) {
        this.interfaze = interfaze;
        this.client = client;
        this.inputProtocol = tProtocol;
        this.outputProtocol = tProtocol;
    }
    public ThriftConfig(Class<T> interfaze, Class<? extends T> client, ProtocolType inputProtocol, ProtocolType outputProtocol) {
        this.interfaze = interfaze;
        this.client = client;
        this.inputProtocol = inputProtocol;
        this.outputProtocol = outputProtocol;
    }

    public Class<T> getInterfaze() {
        return interfaze;
    }

    public Class<? extends T> getClient() {
        return client;
    }

    public ProtocolType getInputProtocol() {
        return inputProtocol;
    }

    public ProtocolType getOutputProtocol() {
        return outputProtocol;
    }
}
