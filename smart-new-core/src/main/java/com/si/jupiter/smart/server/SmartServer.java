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
public class SmartServer {
    private ServerConfig config;
    private Provider[] providers;

    public SmartServer(ServerConfig config, Provider... providers) {
        this.config = config;
        this.providers = providers;
    }

    /**
     * 服务启动，业务线程池，初始化当前服务支持的api,启动netty服务
     */
    public void start() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, config.getCorePoolSize(),
                30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new DefaultThreadFactory("scud-server-work", true), new ThreadPoolExecutor.CallerRunsPolicy());
        ServiceMapper.init(this.providers);
        ServiceInvokeManager manager = new ServiceInvokeManager(config, executor);
        NettyServer.start(this.config, manager);
    }
}
