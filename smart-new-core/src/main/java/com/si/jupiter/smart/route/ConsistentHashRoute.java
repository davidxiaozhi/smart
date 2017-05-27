package com.si.jupiter.smart.route;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.ClusterManagerType;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.cluster.*;
import com.si.jupiter.smart.pools.ShardPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-12 下午8:47
 * Last Update:  17-5-12 下午8:47
 */
public class ConsistentHashRoute extends ShardPoolManager implements RpcRoute{
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsistentHashRoute.class);
    private final ClusterManger clusterManger;
    private static final String KEY_PREFIEX = "ConsistentHashRoute-*";
    public ConsistentHashRoute(ClientConfig clientConfig,ClusterManagerTask managerTask) {
        super(clientConfig,40,true);
        managerTask.setPoolManager(this);//给集群管理任务添加节电池
        if(ClusterManagerType.Local.equals(clientConfig.getClusterManagerType())){
            this.clusterManger = new LocalClusterManager(clientConfig,managerTask);
        }
        else {
            this.clusterManger = new ZkClusterManager(clientConfig, managerTask);
        }
    }

    @Override
    public SmartChannel dispatcher() {
        return SmartChannel.EMPTY;
    }

    @Override
    public SmartChannel dispatcher(String key) {
        //key为空时可以由配置决定是随机key,还是固定key
        ConcurrentSkipListMap<Long,ServerNode> nodes = getServerNodes();
        SortedMap<Long,ServerNode> tail = nodes.tailMap(Long.valueOf(getHashAlgo().hash(KEY_PREFIEX+key)));
        ServerNode serverNode = tail.isEmpty() ?nodes.get(nodes.firstKey()) :  tail.get(tail.firstKey());
        SmartChannel channel = getResource().get(serverNode);
        if(channel!=null){
            return channel;
        }
        else{
            return SmartChannel.EMPTY;
        }
    }
}
