package com.si.jupiter.smart.clent.codec;

import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcInvocation;
import com.si.jupiter.smart.core.SmartRequest;
import com.si.jupiter.smart.network.SerializableEnum;
import com.si.jupiter.smart.network.SerializableHandler;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-12 下午3:07
 * Last Update:  17-6-12 下午3:07
 */
public class ClientProtocolProcesser<T> implements CProtocolProcesser<T> {
    private SerializableEnum serializeType;
    private int requestTimeout;
    public ClientProtocolProcesser(ClientConfig<T> clientConfig) {
        this.serializeType = clientConfig.getSerializeType();
        this.requestTimeout = clientConfig.getRequestTimeout();
    }

    /**
     * 封装客户端RPC调用协议
     *
     * @param smartRequest@return RpcInvocation
     */
    @Override
    public NetworkProtocol buildRequestProtocol(SmartRequest smartRequest) {
        RpcInvocation invocation = new RpcInvocation();
        invocation.setService(smartRequest.getServiceName());
        invocation.setVersion(smartRequest.getVersion());
        invocation.setMethod(smartRequest.getMethod().getName());
        invocation.setArgs(smartRequest.getArgs());
        if (smartRequest.getArgs() != null) {
            String[] argTypes = new String[smartRequest.getArgs().length];
            for (int i = 0; i < smartRequest.getArgs().length; i++) {
                argTypes[i] = smartRequest.getArgs()[i].getClass().getSimpleName();
            }
            invocation.setArgTypes(argTypes);
        }

        NetworkProtocol protocol = new NetworkProtocol();
        protocol.setSerializeType(this.serializeType.getValue());
        protocol.setSequence(smartRequest.getSeq());
        protocol.setRequestTimeout(this.requestTimeout);
        //序列化产生发送消息体二进制
        byte[] content = SerializableHandler.requestEncode(protocol.getSerializeType(), invocation);
        protocol.setContent(content);
        return protocol;
    }

}
