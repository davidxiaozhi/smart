package com.si.jupiter.smart.clent.codec;

import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.SmartRequest;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:20
 *
 */
public interface CProtocolProcesser<T> {
    /**
     * 封装客户端RPC请求协议
     * @param smartRequest@return RpcInvocation
     */
    NetworkProtocol buildRequestProtocol(SmartRequest smartRequest);
}
