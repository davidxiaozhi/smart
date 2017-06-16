package com.si.jupiter.smart.server.codec;

import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcResult;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-15 下午2:13
 * Last Update:  17-6-15 下午2:13
 */
public interface SProtocolProcesser {
    /**
     * 封装服务端RPC响应协议
     *
     * @param requestProtocol 客户端请求时的协议信息
     * @param result          响应数据对象
     * @return NettyProtocol
     */
    NetworkProtocol buildResponseProtocol(NetworkProtocol requestProtocol, RpcResult result);
}
