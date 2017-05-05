package com.si.jupiter.smart.route;

import io.netty.channel.Channel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Author: lizhipeng
 * Date: 2017/01/09 10:44
 */
public abstract class RpcRoute {
    protected Map<String, Channel> serverNodes = new ConcurrentHashMap<>(8);//存放所有服务节点，创建对象时从这里提取
    protected List<Channel> nodes = new CopyOnWriteArrayList<>();//存放所有节点数据

    /**
     * 添加一个服务节点
     *
     * @param node
     */
    public abstract boolean addServerNode(NodeInfo node,Channel channel);

    /**
     * 删除一个服务节点
     *
     * @param path 节点地址
     */
    public abstract boolean removeServerNode(String path);

    /**
     * 获取一个服务节点
     *
     * @return
     */
    public abstract Channel getServer();

    /**
     * 根据指定的key获取服务节点
     *
     * @param key
     * @return
     */
    public Channel getServer(String key) {
        return serverNodes.get(key);
    }

    /**
     * 获取服务节点数
     *
     * @return
     */
    public abstract int size();
}
