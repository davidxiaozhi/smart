package com.si.jupiter.smart.pools;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.route.ServerNode;
import com.si.jupiter.smart.route.ServerStatus;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * todo       : 负责所有client　channel
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-5-23 下午10:32
 * Last Update:  17-5-23 下午10:32
 */
public class ShardPoolManager extends PoolManager{
    private final ConcurrentSkipListMap<Long,ServerNode> nodes;
    private final Hashing algo = Hashing.MURMUR_HASH;
    private final ConcurrentHashMap<ServerNode,SmartChannel> resources;
    private final boolean isNeedWeight;
    private final int RATE;
    public ShardPoolManager(ClientConfig clientConfig,int vNoeRate) {
       this(clientConfig,vNoeRate,false);
    }
    public ShardPoolManager(ClientConfig clientConfig,int vNoeRate,boolean isNeedWeight) {
        super(clientConfig);
        this.RATE = vNoeRate;
        this.resources =  new ConcurrentHashMap<ServerNode,SmartChannel>();;
        nodes = new ConcurrentSkipListMap<Long,ServerNode>();
        this.isNeedWeight = isNeedWeight;
    }

    public void virtualNodeOperator(ServerNode serverNode,ShardType type) {
        int shardWeight;
        if(isNeedWeight){
            shardWeight = serverNode.getWeight();
        }
        else{
            shardWeight = 1;
        }
        for (int n = 0; n < RATE * shardWeight; ++n) {
            StringBuilder builder = new StringBuilder();
            builder.append(serverNode.getServerId()).append("*").append(serverNode.getWeight()).append(n);
            String keyStr = builder.toString();
            Long key = Long.valueOf(this.algo.hash(keyStr));
            if (ShardType.AddNode == type) {
                this.nodes.put(key, serverNode);
            } else {
                this.nodes.remove(key);
            }
        }
    }

    @Override
    public boolean initPoolObject(List<ServerNode> serverNodes) {
        for(ServerNode node:serverNodes){
            addPoolObject(node);
        }
        return false;
    }

    @Override
    public boolean addPoolObject(ServerNode addServerNode) {
        try{
            if(ServerStatus.Serve.equals(addServerNode.getServerStatus())){
                Channel channel = nettyClient.connect(addServerNode.getIp(), addServerNode.getPort());
                resources.put(addServerNode,new SmartChannel(channel, addServerNode));
                virtualNodeOperator(addServerNode,ShardType.AddNode);
                //serverNodes.put(addServerNode.getServerId(),addServerNode);
            }
            else{
                System.out.println("服务器状态为不能提供服务状态.....");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePoolObject(ServerNode updateServerNode) {
        //Channel集合里面没有，并且服务节点状态是堆外提供服务状态，添加
        if(ServerStatus.Serve.equals(updateServerNode.getServerStatus())){//上线状态
            if(!resources.containsKey(updateServerNode)){
                addPoolObject(updateServerNode);//上线操作由addPoolObject负责
            }
        }
        if(!ServerStatus.Serve.equals(updateServerNode.getServerStatus())){
            removePoolObject(updateServerNode);//下线操作由removeObject进行
        }
        return false;
    }

    @Override
    public boolean removePoolObject(ServerNode removeServerNode) {
        try{
            virtualNodeOperator(removeServerNode,ShardType.DeleteNode);
            SmartChannel removeChannel = resources.remove(removeServerNode);
            if(removeChannel!=null&&removeChannel.getNettyChannel().isActive()){
                removeChannel.getNettyChannel().close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public ConcurrentSkipListMap<Long,ServerNode> getServerNodes(){
        return nodes;
    }
    public List<Long> getServerNodesIds(){
        ArrayList<Long> ids = new ArrayList<Long>(nodes.size());
        for(Long id:nodes.keySet()){
            ids.add(id);
        }
        return ids;
    }
    public ConcurrentHashMap<ServerNode,SmartChannel> getResource(){
        return resources;
    }
    public Hashing getHashAlgo(){
        return algo;
    }
}
