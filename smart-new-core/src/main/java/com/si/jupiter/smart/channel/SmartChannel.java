package com.si.jupiter.smart.channel;

import com.si.jupiter.smart.route.ServerNode;
import io.netty.channel.Channel;

/**
 * todo       : 针对 netty Channel进行分装
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-19 下午3:09
 * Last Update:  17-5-19 下午3:09
 */
public class SmartChannel {
    public static final SmartChannel EMPTY = new SmartChannel(null,null);
    private final Channel nettyChannel;
    private final ServerNode serverNode;
    public SmartChannel(Channel nettyChannel, ServerNode serverNode) {
        this.nettyChannel = nettyChannel;
        this.serverNode = serverNode;
    }

    public Channel getNettyChannel() {
        return nettyChannel;
    }

    public ServerNode getServerNode() {
        return serverNode;
    }
}
