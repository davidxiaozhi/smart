package com.si.jupiter.smart.cluster;

import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.route.ServerNode;
import com.si.jupiter.smart.route.ServerStatus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * todo       : 本地集群管理
 * 　　　　　　　　　　　通过定时任务定时更新集群
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-19 上午11:31
 * Last Update:  17-5-19 上午11:31
 */
public class LocalClusterManager extends ClusterManger {

    private ScheduledExecutorService managerTask;
    public LocalClusterManager(final ClientConfig clientConfig, final ClusterManagerTask clusterManagerTask) {
        super(clientConfig, clusterManagerTask);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("-***-local cluster manager update the server info ");
                updatePool(clientConfig);
            }
        };
        managerTask = Executors.newScheduledThreadPool(1);
        managerTask.scheduleAtFixedRate(runnable, 1, 30, TimeUnit.SECONDS);
    }

    public void updatePool(ClientConfig clientConfig){
        String[] hosts = clientConfig.getHost().split(";");
        for (String host : hosts) {
            String[] ipPort = host.trim().split(":");
            String ip = ipPort[0];
            int port = Integer.parseInt(ipPort[1]);
            ServerNode serverNode = new ServerNode(ip, port);
            serverNode.setServerStatus(ServerStatus.Serve);
            getClusterManagerTask().updateClusterNode(serverNode);
        }
    }
}
