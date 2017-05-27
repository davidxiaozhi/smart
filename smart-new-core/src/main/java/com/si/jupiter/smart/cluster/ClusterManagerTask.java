package com.si.jupiter.smart.cluster;

import com.si.jupiter.smart.pools.PoolManager;
import com.si.jupiter.smart.route.ServerNode;

import java.util.List;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-16 下午3:00
 * Last Update:  17-5-16 下午3:00
 */
public abstract class ClusterManagerTask {

    private PoolManager poolManager;

    public PoolManager getPoolManager() {
        return poolManager;
    }

    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    /**
     * 初始化　对象池当中的对象
     * @param serverNodes
     * @return
     */
    public abstract boolean init(List<ServerNode> serverNodes);

    /**
     * 添加服务节点
     * @param addNode
     * @return
     */
    public abstract boolean addClusterNode(ServerNode addNode);

    /**
     * 更新服务节点
     * @param updateNode
     * @return
     */
    public abstract boolean updateClusterNode(ServerNode updateNode);

    /**
     * 移除服务节点
     * @param removeNode
     * @return
     */
    public abstract boolean removeClusterNode(ServerNode removeNode);


}
