package com.si.jupiter.smart.route;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.pools.PoolManager;
import io.netty.channel.Channel;

/**
 * Author: lizhipeng
 * Date: 2017/01/09 10:44
 */
public interface RpcRoute {


   SmartChannel dispatcher();

    /**
     * 根据指定的key获取服务节点
     *
     * @param key
     * @return
     */
    SmartChannel dispatcher(String key);
}
