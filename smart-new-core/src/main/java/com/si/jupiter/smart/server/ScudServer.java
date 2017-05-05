package com.si.jupiter.smart.server;

import com.si.jupiter.smart.core.ServiceMapper;
import com.si.jupiter.smart.network.netty.NettyServer;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: baichuan - lizhipeng
 * Date: 2017/01/04 09:40
 */
public class ScudServer {
    private ServerConfig config;
    private Provider[] providers;

    public ScudServer(ServerConfig config, Provider... providers) {
        this.config = config;
        this.providers = providers;
    }

    public void start() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, config.getCorePoolSize(),
                30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new DefaultThreadFactory("scud-server-work", true), new ThreadPoolExecutor.CallerRunsPolicy());
        ServiceMapper.init(this.providers);
        ServerManager manager = new ServerManager(config, executor);
        NettyServer.start(this.config, manager);
    }
}
