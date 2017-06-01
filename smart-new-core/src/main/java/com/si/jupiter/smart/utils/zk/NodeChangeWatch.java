package com.si.jupiter.smart.utils.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * zookeep 具体node值变化监听器
 */
public class NodeChangeWatch {
    public static final Logger logger = LoggerFactory.getLogger(NodeChangeWatch.class);

    private String  zkServers;
    private Map<String,String> conf;
    private CuratorFramework _curator;
    private NodeCache _cache;
    private String  nodePathWatch;
    private NodeChangeDeal nodeChangeDeal;
    private ZkOperator _operator;

    public NodeChangeWatch(){
    }

    public NodeChangeWatch(String zkServers){
        this.zkServers = zkServers;
    }

    public NodeChangeWatch(Map<String,String> conf){
        this.conf = conf;
    }

    public NodeChangeWatch(ZkOperator _operator){
        this._operator = _operator;
        this._curator=_operator.getCurator();
    }

    public String getZkServers() {
        return zkServers;
    }

    public CuratorFramework get_curator() {
        return _curator;
    }

    public NodeCache get_cache() {
        return _cache;
    }

    public String getNodePathWatch() {
        return nodePathWatch;
    }

    public ZkOperator get_operator() {
        return _operator;
    }

    /**
     * 添加zookeeper severs
     * @param zkServers
     * @return
     */
    public NodeChangeWatch addZkServers(String zkServers){
        this.zkServers=zkServers;
        return this;
    }

    /**
     * 添加需要监控的 zookeeper node path
     * @param nodePathWatch
     * @return
     */
    public NodeChangeWatch addNodePath(String nodePathWatch){
        this.nodePathWatch=nodePathWatch;
        return this;
    }

    /**
     * 当节点value值发生改变时如何处理
     * @param deal
     * @return
     */
    public NodeChangeWatch whenNodeChangeDo(NodeChangeDeal deal){
        this.nodeChangeDeal =deal;
        return this;
    }
    public void start() throws Exception {
        assert nodePathWatch!=null&&!"".equals(nodePathWatch.trim());
        if(_operator==null){
            if(conf!=null){
                if(zkServers!=null){
                    conf.put("zookeeper.zkservers",zkServers);
                }
                _operator =new ZkOperator(conf);

            }
            else {
                assert zkServers != null;
                _operator =new ZkOperator(zkServers);
            }
            _curator = _operator.getCurator();
        }
        this._cache = new NodeCache(_curator,nodePathWatch);

        this._cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
               nodeChangeDeal.deal(_cache.getCurrentData().getData());
            }
        });
        this._cache.start();
    }
    public void stop(){
        if(_cache!=null){
            CloseableUtils.closeQuietly(_cache);
        }
        if(_curator!=null){
            CloseableUtils.closeQuietly(_curator);
        }
    }


}
