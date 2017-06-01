package com.si.jupiter.smart.cluster;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.si.jupiter.smart.utils.zk.PathChangeDeal;
import com.si.jupiter.smart.utils.zk.PathChangeWatch;
import com.si.jupiter.smart.utils.zk.PathNode;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.route.ServerNode;
import com.si.jupiter.smart.utils.zk.ZkOperator;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-12 下午3:59
 * Last Update:  17-5-12 下午3:59
 */
public class ZkClusterManager extends ClusterManger {
    public final ZkOperator zkOperator;
    public ZkClusterManager(ClientConfig clientConfig, final ClusterManagerTask clusterManagerTask ) {
        super(clientConfig,clusterManagerTask);
        zkOperator = new ZkOperator("127.0.0.1:2181");
        final PathChangeWatch watch = new PathChangeWatch(zkOperator);
        watch.addNodePath("/si/jupiter/smart/testServer/rpc")
                .whenNodeChangeDo(new PathChangeDeal() {
                    @Override
                    public void deal(PathNode pathNode) {
                        //添加服务节点
                        if(PathChildrenCacheEvent.Type.CHILD_ADDED.equals(pathNode.getEvenType())){
                            String nodeInfoStr = pathNode.getNode();
                            ServerNode serverNode = contentToNode(nodeInfoStr);
                            if(serverNode !=null){
                                clusterManagerTask.addClusterNode(serverNode);
                            }
                        }
                        //移除服务节点
                        else if(PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(pathNode.getEvenType())){
                            String nodeInfoStr = pathNode.getNode();
                            ServerNode serverNode = contentToNode(nodeInfoStr);
                            if(serverNode !=null){
                                clusterManagerTask.removeClusterNode(serverNode);
                            }
                        }
                        //更新服务节点
                        else if(PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(pathNode.getEvenType())){
                            String nodeInfoStr = pathNode.getNode();
                            ServerNode serverNode = contentToNode(nodeInfoStr);
                            if(serverNode !=null){
                                clusterManagerTask.updateClusterNode(serverNode);
                            }
                        }
                        else if(PathChildrenCacheEvent.Type.CONNECTION_LOST.equals(pathNode.getEvenType())){
                            System.out.println("zookeeper connection lost .....");
                        }
                        else if(PathChildrenCacheEvent.Type.CONNECTION_RECONNECTED.equals(pathNode.getEvenType())){
                            System.out.println("zookeeper reconnected .....");
                        }
                    }
                });
        try {
            watch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ServerNode contentToNode(String content){
        try {
            ServerNode serverNode = JSON.parseObject(content, ServerNode.class);
            return serverNode;
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("content to json object fail!!!!!");
        return null;
    }


}
