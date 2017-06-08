package com.si.jupiter.smart.route;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.ClusterManagerType;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.cluster.*;
import com.si.jupiter.smart.pools.PoolManager;
import com.si.jupiter.smart.pools.ShardPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-22 上午10:40
 * Last Update:  17-5-22 上午10:40
 */
public class RoundRoute extends ShardPoolManager implements RpcRoute {
    private final static Logger LOGGER = LoggerFactory.getLogger(RoundRoute.class);
    private final AtomicPositiveInteger atomicPositiveInteger = new AtomicPositiveInteger();
    private final ClusterManger clusterManger;
    public RoundRoute(ClientConfig clientConfig,ClusterManagerTask managerTask) {
        super(clientConfig,1);
        managerTask.setPoolManager(this);//给集群管理任务添加节电池
        if(ClusterManagerType.Local.equals(clientConfig.getClusterManagerType())){
            this.clusterManger = new LocalClusterManager(clientConfig,managerTask);
        }
        else {
            this.clusterManger = new ZkClusterManager(clientConfig, managerTask);
        }
    }

    protected Long doSelect(List<Long> serverIds) {
        if(serverIds==null||serverIds.size()==0){
            return -1L;
        }
        int length = serverIds.size();
        if (length == 1) {
            return serverIds.get(0);
        }
        // 取模轮循
        return   serverIds.get(atomicPositiveInteger.getAndIncrement() % length);
    }

    @Override
    public SmartChannel dispatcher() {
        List<Long> ids = getServerNodesIds();
        ConcurrentSkipListMap<Long,ServerNode> nodes = getServerNodes();
        Long serverId = doSelect(ids);
        //也可以借用一致hash的思想，规避风险，或者不同步的问题
        ServerNode serverNode = nodes.get(serverId);
        SmartChannel smartChannel = getResource().get(serverNode);
        if(smartChannel==null){
            return SmartChannel.EMPTY;
        }
        return smartChannel;
    }

    @Override
    public SmartChannel dispatcher(String key) {
        return dispatcher();
    }

}
