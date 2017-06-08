package com.si.jupiter.smart.cluster;

import com.alibaba.fastjson.JSON;
import com.si.jupiter.smart.utils.zk.ZkOperator;
import com.si.jupiter.smart.route.ServerNode;
import com.si.jupiter.smart.server.ServerConfig;
import org.apache.zookeeper.CreateMode;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-15 下午2:38
 * Last Update:  17-5-15 下午2:38
 */
public class ZkRegistCenter extends ServerRegistCenter{
    public final ZkOperator zkOperator;
    private final String registPath;
    public ZkRegistCenter(ServerConfig serverConfig) {
        super(serverConfig);
        //以下
        this.zkOperator = new ZkOperator("127.0.0.1:2181");
        this.registPath = "/si/jupiter/smart/testServer/rpc";
    }

    public boolean regist(ServerNode registNode){
        try{
            String serverId=registNode.getServerId();
            String value = JSON.toJSONString(registNode);
            this.zkOperator.writeString(registPath+"/"+serverId,value, CreateMode.EPHEMERAL);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    public boolean update(ServerNode updateNode){
        try{
            String serverId=updateNode.getServerId();
            String value = JSON.toJSONString(updateNode);
            String oldValue = this.zkOperator.readString(registPath+"/"+serverId);
            ServerNode oldNode = (ServerNode) JSON.parse(oldValue);
            if(updateNode!=null&&updateNode.getServerId()!=null&&updateNode.getServerId().equals(oldNode.getServerId())){
                this.zkOperator.writeString(registPath+"/"+serverId,value, CreateMode.EPHEMERAL);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }
    public boolean remove(ServerNode removeNode){
        try{
            String serverId=removeNode.getServerId();
            if(removeNode!=null&&removeNode.getServerId()!=null){
                this.zkOperator.deleteZkNode(registPath+"/"+serverId);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }
}
