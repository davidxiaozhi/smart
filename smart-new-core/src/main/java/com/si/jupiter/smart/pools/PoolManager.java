package com.si.jupiter.smart.pools;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.network.netty.NettyClient;
import com.si.jupiter.smart.route.ServerNode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-18 上午11:09
 * Last Update:  17-5-18 上午11:09
 */
public abstract class PoolManager {
    protected final NettyClient nettyClient;
    protected PoolManager(ClientConfig clientConfig) {
        this.nettyClient = new NettyClient(clientConfig);
    }

    public abstract boolean initPoolObject(List<ServerNode> serverNodes);

    public abstract boolean addPoolObject(ServerNode addServerNode);

    public abstract boolean updatePoolObject(ServerNode updateServerNode);

    public abstract boolean removePoolObject(ServerNode removeServerNode);

}
