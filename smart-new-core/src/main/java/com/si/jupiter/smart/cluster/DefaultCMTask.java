package com.si.jupiter.smart.cluster;

import com.si.jupiter.smart.pools.PoolManager;
import com.si.jupiter.smart.route.ServerNode;
import com.si.jupiter.smart.route.ServerStatus;

import java.util.List;

/**
 * todo       : 集群管理任务，用于集群管理中心的回调操作
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-22 上午11:04
 * Last Update:  17-5-22 上午11:04
 */
public class DefaultCMTask extends ClusterManagerTask {
    @Override
    public boolean init(List<ServerNode> serverNodes) {
        try {
            for (ServerNode serverNode : serverNodes) {
                getPoolManager().addPoolObject(serverNode);
                //sharded.virtualNodeOperator(new ShardNodeInfo(serverNode), ShardType.AddNode);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean addClusterNode(ServerNode addNode) {
        try {
            //先添加池
            getPoolManager().addPoolObject(addNode);
            //sharded.virtualNodeOperator(new ShardNodeInfo(addNode), ShardType.AddNode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateClusterNode(ServerNode updateNode) {
        try {
            if(ServerStatus.NoServe.equals(updateNode.getServerStatus())){
                removeClusterNode(updateNode);
            }
            else if(ServerStatus.Serve.equals(updateNode.getServerStatus())){
                //更新操作这里初步每次重建ShardPool,以防止权重发生变化
                //sharded.virtualNodeOperator(new ShardNodeInfo(updateNode), ShardType.DeleteNode);
                //sharded.virtualNodeOperator(new ShardNodeInfo(updateNode), ShardType.AddNode);
                //由池管理自己决定如何进行更新操作
                getPoolManager().updatePoolObject(updateNode);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean removeClusterNode(ServerNode removeNode) {
        try {
            //先remove入口
            //sharded.virtualNodeOperator(new ShardNodeInfo(removeNode), ShardType.DeleteNode);
            getPoolManager().removePoolObject(removeNode);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
