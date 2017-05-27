package com.si.jupiter.smart.core;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:20
 *
 */
public interface ProtocolProcesser {
    /**
     * 封装客户端RPC请求协议
     * @param smartRequest@return RpcInvocation
     */
    NetworkProtocol buildRequestProtocol(SmartRequest smartRequest);
}
