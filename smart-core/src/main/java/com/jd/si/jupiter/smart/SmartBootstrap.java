/*
 * Copyright (C) 2012-2013 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jd.si.jupiter.smart;

import com.google.common.collect.ImmutableMap;
import com.jd.si.jupiter.smart.core.NettyServerConfig;
import com.jd.si.jupiter.smart.core.NettyServerTransport;
import com.jd.si.jupiter.smart.core.ShutdownUtil;
import com.jd.si.jupiter.smart.core.ThriftServerDef;
import com.jd.si.jupiter.smart.statistics.SmartMetrics;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 * 引导类,用来管理多个核心channel的启动和停止
 *
 */

public class SmartBootstrap
{
    private final ChannelGroup allChannels;//可以在生命周期内维护多个channel,可以多个channel同时读写
    private final NettyServerConfig nettyServerConfig;//服务bootstrap配置信息
    private final Map<ThriftServerDef, NettyServerTransport> transports;
    private ExecutorService bossExecutor;
    private ExecutorService workerExecutor;
    //private NioServerSocketChannelFactory serverChannelFactory;

    /**
     *
     * @param thriftServerDefs thrift服务相关定义组合
     * @param nettyServerConfig netty Nio 服务端 bootstrap相关配置信息
     * @param allChannels channel组合
     */
    public SmartBootstrap(
            Set<ThriftServerDef> thriftServerDefs,
            NettyServerConfig nettyServerConfig,
            ChannelGroup allChannels)
    {
        this.allChannels = allChannels;
        //维护每一个ThrfitSeverDef与它的NettyServerTransport
        ImmutableMap.Builder<ThriftServerDef, NettyServerTransport> builder = new ImmutableMap.Builder<ThriftServerDef, NettyServerTransport>();
        this.nettyServerConfig = nettyServerConfig;
        //处理每一个thrift服务定义定义
        for (ThriftServerDef thriftServerDef : thriftServerDefs) {
            builder.put(thriftServerDef,
                    new NettyServerTransport(thriftServerDef,nettyServerConfig,allChannels));
        }
        transports = builder.build();
    }

    public void start() throws InterruptedException {
        for (NettyServerTransport transport : transports.values()) {
            transport.start();
        }
    }

    public void stop()
    {
        for (NettyServerTransport transport : transports.values()) {
            try {
                transport.stop();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Map<ThriftServerDef, Integer> getBoundPorts()
    {
        ImmutableMap.Builder<ThriftServerDef, Integer> builder = new ImmutableMap.Builder<ThriftServerDef, Integer>();
        for (Map.Entry<ThriftServerDef, NettyServerTransport> entry : transports.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().getPort());
        }
        return builder.build();
    }

    public Map<ThriftServerDef, SmartMetrics> getSmartMetrics()
    {
        ImmutableMap.Builder<ThriftServerDef, SmartMetrics> builder = new ImmutableMap.Builder<ThriftServerDef, SmartMetrics>();
        for (Map.Entry<ThriftServerDef, NettyServerTransport> entry : transports.entrySet()) {
            builder.put(entry.getKey(), entry.getValue().getMetrics());
        }
        return builder.build();
    }
}
