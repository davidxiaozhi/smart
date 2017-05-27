package com.si.jupiter.smart.cluster;

import com.si.jupiter.smart.clent.config.ClientConfig;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-12 下午3:58
 * Last Update:  17-5-12 下午3:58
 */
public abstract class ClusterManger {
    private ClientConfig clientConfig;
    private ClusterManagerTask clusterManagerTask;

    public ClusterManger(ClientConfig clientConfig,final ClusterManagerTask clusterManagerTask) {
        this.clientConfig = clientConfig;
        this.clusterManagerTask=clusterManagerTask;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }

    public ClusterManagerTask getClusterManagerTask() {
        return clusterManagerTask;
    }
}
