package com.si.jupiter.smart.utils.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by davidxiaozhi on 14-9-18.
 */
public class PathChangeWatch {
    public static final Logger logger = LoggerFactory.getLogger(PathChangeWatch.class);

    private String zkServers;
    private Map<String,String> conf;
    private CuratorFramework _curator;
    private PathChildrenCache _cache;
    private String pathWatch;
    private boolean isCacheData = false;
    private PathChangeDeal pathChangeDeal;
    private ZkOperator _operator;

    public PathChangeWatch() {
    }

    public PathChangeWatch(String zkServers){
        this.zkServers = zkServers;
    }

    public PathChangeWatch(Map<String,String> conf){
        this.conf = conf;
    }

    public PathChangeWatch(ZkOperator _operator){
        this._operator = _operator;
        this._curator=_operator.getCurator();
    }

    public String getZkServers() {
        return zkServers;
    }

    public CuratorFramework get_curator() {
        return _curator;
    }

    public PathChildrenCache get_cache() {
        return _cache;
    }

    public String getPathWatch() {
        return pathWatch;
    }

    public ZkOperator get_operator() {
        return _operator;
    }

    /**
     * 添加zookeeper severs
     *
     * @param zkServers
     * @return
     */
    public PathChangeWatch addZkServers(String zkServers) {
        this.zkServers = zkServers;
        return this;
    }

    /**
     * 添加需要监控的 zookeeper node path
     *
     * @param pathWatch
     * @return
     */
    public PathChangeWatch addNodePath(String pathWatch) {
        this.pathWatch = pathWatch;
        return this;
    }

    /**
     * 是否需要缓存数据
     *
     * @param isCacheData
     * @return
     */
    public PathChangeWatch isCacheData(boolean isCacheData) {
        this.isCacheData = isCacheData;
        return this;
    }

    /**
     * 当节点value值发生改变时如何处理
     *
     * @param deal
     * @return
     */
    public PathChangeWatch whenNodeChangeDo(PathChangeDeal deal) {
        this.pathChangeDeal = deal;
        return this;
    }

    public void start() throws Exception {

        assert pathWatch != null && !"".equals(pathWatch.trim());
        assert pathChangeDeal != null;

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
        //zookeeper.zkservers

        this._cache = new PathChildrenCache(_curator, pathWatch, this.isCacheData);

        //this._cache.getListenable().addListener();
        PathChildrenCacheListener listener = getDefaultPathChildrenCacheListener(this.getPathWatch());
        _cache.getListenable().addListener(listener);

        this._cache.start();
    }
    public void stop() {
        if(_cache!=null){
            CloseableUtils.closeQuietly(_cache);
        }
        if(_curator!=null){
            CloseableUtils.closeQuietly(_curator);
        }

    }
    private PathChildrenCacheListener getDefaultPathChildrenCacheListener(final String pathWatch) {
        return new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED: {

                        String path = event.getData().getPath();
                        PathNode pathNode = PathNode.getPathAndNode(path, PathChildrenCacheEvent.Type.CHILD_ADDED);
                        logger.debug("Node:" + pathNode.getPath() + "  added: " + pathNode.getNode());
                        pathChangeDeal.deal(pathNode);
                        break;
                    }

                    case CHILD_UPDATED: {

                        String path = event.getData().getPath();
                        PathNode pathNode = PathNode.getPathAndNode(path, PathChildrenCacheEvent.Type.CHILD_UPDATED);
                        logger.debug("Node:" + pathNode.getPath() + "  changed: " + pathNode.getNode());
                        pathChangeDeal.deal(pathNode);
                        break;
                    }

                    case CHILD_REMOVED: {
                        String path = event.getData().getPath();
                        PathNode pathNode = PathNode.getPathAndNode(path, PathChildrenCacheEvent.Type.CHILD_REMOVED);
                        logger.debug("Node:" + pathNode.getPath() + "  remove: " + pathNode.getNode());
                        pathChangeDeal.deal(pathNode);
                        break;
                    }
                    case CONNECTION_LOST:{
                        PathNode pathNode = new PathNode(null,null, PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED);
                        logger.debug("Node:" +pathWatch+" CONNECTION_LOST: ");
                        break;
                    }
                    case CONNECTION_RECONNECTED:{
                        PathNode pathNode = new PathNode(null,null, PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED);
                        logger.debug("Node:" +pathWatch+" CONNECTION_RECONNECTED: ");
                        break;
                    }
                    case CONNECTION_SUSPENDED:{
                        PathNode pathNode = new PathNode(null,null, PathChildrenCacheEvent.Type.CONNECTION_SUSPENDED);
                        logger.debug("Node:" +pathWatch+" CONNECTION_SUSPENDED: ");
                        pathChangeDeal.deal(pathNode);
                        break;
                    }
                }
            }
        };
    }
}
