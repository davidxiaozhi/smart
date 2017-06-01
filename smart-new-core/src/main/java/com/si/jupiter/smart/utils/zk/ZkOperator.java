/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.si.jupiter.smart.utils.zk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.utils.CloseableUtils;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZkOperator {
    public static final Logger logger = LoggerFactory.getLogger(ZkOperator.class);
    private CuratorFramework _curator;
    private int zookeeperSessionTimeout = 10000;
    private int zookeeperRetryTimes = 3000;
    private int zookeeperRetryInterval = 5000;
    private int zookeeperRetryMaxInterval = 120000;
    private int zookeeperConnectionTimeOut = 15000;

    public static final String ZOOKEEPER_SERVERS="zookeeper.zkservers";
    public static final String ZOOKEEPER_SESSION_TIMEOUT="zookeeper.sessionTimeout";
    public static final String ZOOKEEPER_MAX_INTERVAL="zookeeper.maxTimeInterval";
    public static final String ZOOKEEPER_RETRY_TIMES="zookeeper.retryTimes";
    public static final String ZOOKEEPER_RETRY_INTERVAL="zookeeper.retryInterval";
    public static final String ZOOKEEPER_CONNECT_TIMEOUT="zookeeper.connectTimeout";


    public enum  ConnectionRetryPolicy{
        RetryNTimes(1),
        RetryOneTime(2),
        ExponentialBackoffRetry(3);

        ConnectionRetryPolicy(int value){
            this.value=value;
        }
        private final int value;

    }

    private CuratorFramework newCurator(Map stateConf,ConnectionRetryPolicy connectionRetryPolicy) throws Exception {
        String zkServers = (String) stateConf.get(ZOOKEEPER_SERVERS);
        if (stateConf.containsKey(ZOOKEEPER_SESSION_TIMEOUT)) {
            zookeeperSessionTimeout = Integer.valueOf(stateConf.get(ZOOKEEPER_SESSION_TIMEOUT).toString());
        }
        if (stateConf.containsKey(ZOOKEEPER_RETRY_TIMES)) {
            zookeeperRetryTimes = Integer.valueOf(stateConf.get(ZOOKEEPER_RETRY_TIMES).toString());
        }
        if (stateConf.containsKey(ZOOKEEPER_RETRY_INTERVAL)) {
            zookeeperRetryInterval = Integer.valueOf(stateConf.get(ZOOKEEPER_RETRY_INTERVAL).toString());
        }
        if (stateConf.containsKey(ZOOKEEPER_CONNECT_TIMEOUT)) {
            zookeeperConnectionTimeOut  = Integer.valueOf(stateConf.get(ZOOKEEPER_CONNECT_TIMEOUT).toString());
        }
        if (stateConf.containsKey(ZOOKEEPER_MAX_INTERVAL)) {
           zookeeperRetryMaxInterval = Integer.valueOf(stateConf.get(ZOOKEEPER_MAX_INTERVAL).toString());
        }
        //链接断开后，依据不同的策略进行重试
        //重试一次
        if(ConnectionRetryPolicy.RetryOneTime==connectionRetryPolicy){
            logger.debug("init zookeeper,when connection was lost,the policy of retry is "+ ConnectionRetryPolicy.RetryOneTime.name());
           return  CuratorFrameworkFactory.newClient(zkServers,
                    zookeeperSessionTimeout,//sesstion超时时间
                    zookeeperConnectionTimeOut,//连接超时时间
                    new RetryOneTime(zookeeperRetryInterval)//重试策略
            );
        }
        //重试间隔指数指数变化
        else if(ConnectionRetryPolicy.ExponentialBackoffRetry== connectionRetryPolicy){
            logger.debug("init zookeeper,when connection was lost,the policy of retry is "+ ConnectionRetryPolicy.ExponentialBackoffRetry.name());
            return CuratorFrameworkFactory.newClient(zkServers,
                    zookeeperSessionTimeout,//sesstion超时时间
                    zookeeperConnectionTimeOut,//连接超时时间
                    new ExponentialBackoffRetry(zookeeperRetryInterval, zookeeperRetryTimes,zookeeperRetryMaxInterval)//重试策略
            );
        }
        //重试n次
        else{
            logger.debug("init zookeeper,when connection was lost,the policy of retry is "+ ConnectionRetryPolicy.RetryNTimes.name());
            return CuratorFrameworkFactory.newClient(zkServers,
                    zookeeperSessionTimeout,//sesstion超时时间
                    zookeeperConnectionTimeOut,//连接超时时间
                    new RetryNTimes(zookeeperRetryTimes, zookeeperRetryInterval)//重试策略
            );
        }

    }

    private CuratorFramework newCurator(String zkServers) throws Exception {
        logger.debug("init zookeeper,when connection was lost,the policy of retry is "+ ConnectionRetryPolicy.RetryNTimes.name());
        return CuratorFrameworkFactory.newClient(zkServers,
                zookeeperSessionTimeout,//sesstion超时时间
                zookeeperConnectionTimeOut,//连接超时时间
                new RetryNTimes(zookeeperRetryTimes, zookeeperRetryInterval)//重试策略
        );
    }

    public CuratorFramework getCurator() {
        assert _curator != null;
        return _curator;
    }
    public ZkOperator(String zkServers) {
        try {
            _curator = newCurator(zkServers);
            _curator.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ZkOperator(Map stateConf) {
        try {
            _curator = newCurator(stateConf, ConnectionRetryPolicy.RetryNTimes);
            _curator.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ZkOperator(Map stateConf,ConnectionRetryPolicy connectionRetryPolicy) {
        try {
            _curator = newCurator(stateConf,connectionRetryPolicy);
            _curator.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public void writeJSON(String path, Map<Object, Object> data) {
        logger.debug("Writing " + path + " the data " + data.toString());
        writeBytes(path, JSONValue.toJSONString(data).getBytes(Charset.forName("UTF-8")));
    }

    public void writeJSON(String path, Map<Object, Object> data, CreateMode createModeType) {
        logger.debug("Writing " + path + " the data " + data.toString());
        writeBytes(path, JSONValue.toJSONString(data).getBytes(Charset.forName("UTF-8")), createModeType);
    }*/

    public void writeString(String path, String data) {
        logger.debug("Writing " + path + " the data " + data.toString());
        writeBytes(path, data.getBytes(Charset.forName("UTF-8")));
    }

    public void writeString(String path, String data, CreateMode createModeType) {
        logger.debug("Writing " + path + " the data " + data.toString());
        writeBytes(path, data.getBytes(Charset.forName("UTF-8")), createModeType);
    }

    public void writeBytes(String path, byte[] bytes) {
        writeBytes(path, bytes, CreateMode.PERSISTENT);

    }

    /**
     * 创建节点
     *
     * @param path
     * @param bytes
     * @param createModeType CreateMode: PERSISTENT, PERSISTENT_SEQUENTIAL, EPHEMERAL, EPHEMERAL_SEQUENTIAL
     */
    public void writeBytes(String path, byte[] bytes, CreateMode createModeType) {
        try {
            if (_curator.checkExists().forPath(path) == null) {
                _curator.create()
                        .creatingParentsIfNeeded()
                        .withMode(createModeType)
                        .forPath(path, bytes);
            } else {
                _curator.setData().forPath(path, bytes);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /*public Map<Object, Object> readJSON(String path) {
        try {
            byte[] b = readBytes(path);
            if (b == null) {
                return null;
            }
            return JSON.parseObject(new String(b, "UTF-8"),new TypeReference<Map<Object, Object>>(){} );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    public String readString(String path) {
        try {
            byte[] b = readBytes(path);
            if (b == null) {
                return null;
            }
            return new String(b);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] readBytes(String path) {
        try {
            if (_curator.checkExists().forPath(path) != null) {
                return _curator.getData().forPath(path);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (_curator != null) {
                CloseableUtils.closeQuietly(_curator);
            }
            _curator = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean checkExists(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) != null) {
            return true;
        }
        return false;

    }

    public List<String> getChildNodeName(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) == null) {
            return new ArrayList<String>();
        }
        List<String> children = _curator.getChildren().forPath(zkPath);
        return children;
    }

    public List<String> getChildNodeFullName(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) == null) {
            return new ArrayList<String>();
        }
        List<String> children = _curator.getChildren().forPath(zkPath);
        List<String> childrenFullName = new ArrayList<String>(children.size());
        for (String node : children) {
            if ("/".equals(zkPath)) {
                childrenFullName.add(zkPath + "" + node);
            } else {
                childrenFullName.add(zkPath + "/" + node);
            }

        }
        return childrenFullName;
    }

    public void createZkPath(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) == null) {
            _curator.create().creatingParentsIfNeeded().forPath(zkPath);
        }
    }

    public void deleteZkNode(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) != null) {
            _curator.delete().forPath(zkPath);
        }
    }

    public void deleteZkNodeWithChildren(String zkPath) throws Exception {
        if (_curator.checkExists().forPath(zkPath) != null) {
            _curator.delete().deletingChildrenIfNeeded().forPath(zkPath);
        }
    }

}
