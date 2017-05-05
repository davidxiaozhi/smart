package com.si.jupiter.smart.clent;

import com.si.jupiter.smart.core.NetworkProtocol;
import io.netty.channel.Channel;

/**
 * Author: lizhipeng
 * Date: 2017/01/08 10:21
 */
public interface Invoker {
    void invoke(Channel ch, NetworkProtocol protocol, int packageId) throws Exception;
}
