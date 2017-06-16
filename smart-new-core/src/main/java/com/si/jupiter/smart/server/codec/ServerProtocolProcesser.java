package com.si.jupiter.smart.server.codec;

import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcResult;
import com.si.jupiter.smart.network.SerializableHandler;

/**
 * todo       : 服务端网络协议处理的默认类
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-15 下午2:17
 * Last Update:  17-6-15 下午2:17
 */
public class ServerProtocolProcesser implements SProtocolProcesser {
    @Override
    public NetworkProtocol buildResponseProtocol(NetworkProtocol requestProtocol, RpcResult result) {
        byte[] content = SerializableHandler.responseEncode(requestProtocol.getSerializeType(), result);
        NetworkProtocol protocol = new NetworkProtocol();
        protocol.setContent(content);
        protocol.setSequence(requestProtocol.getSequence());
        protocol.setSerializeType(requestProtocol.getSerializeType());
        protocol.setProtocolVersion(requestProtocol.getProtocolVersion());
        return protocol;
    }
}
