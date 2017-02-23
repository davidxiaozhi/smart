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
package com.jd.si.jupiter.smart.core;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.Timer;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import static java.util.concurrent.Executors.newCachedThreadPool;

/*
 * Netty Server配置参数构建工厂
 * 升级至4.0版本变更NioServerSocketChannelConfig 变更为 SocketChannelConfig
 */
public class NettyServerConfigBuilder extends NettyConfigBuilderBase<NettyServerConfigBuilder>
{
    //通过代理配置socket的选项配置
    private final SocketChannelConfig socketChannelConfig = (SocketChannelConfig) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[]{SocketChannelConfig.class},
            new Magic("child.")
    );
    //通过代理配置serverSocket的选项配置
    private final ServerSocketChannelConfig serverSocketChannelConfig = (ServerSocketChannelConfig) Proxy.newProxyInstance(
            getClass().getClassLoader(),
            new Class<?>[]{ServerSocketChannelConfig.class},
            new Magic(""));

    public NettyServerConfigBuilder()
    {
        // Thrift turns TCP_NODELAY by default, and turning it off can have latency implications
        // so let's turn it on by default as well. It can still be switched off by explicitly
        // calling setTcpNodelay(false) after construction.
        getSocketChannelConfig().setTcpNoDelay(true);
    }

    public SocketChannelConfig getSocketChannelConfig()
    {
        return socketChannelConfig;
    }

    public ServerSocketChannelConfig getServerSocketChannelConfig()
    {
        return serverSocketChannelConfig;
    }
    //构建netty服务配置
    public NettyServerConfig build()
    {
        Timer timer = getTimer();
        int bossThreadCount = getBossThreadCount();
        int workerThreadCount = getWorkerThreadCount();

        return new NettyServerConfig(
                getBootstrapOptions(),
                timer != null ? timer : new SmartTimer(threadNamePattern("")),
                bossThreadCount,
                workerThreadCount
        );
    }

    private ExecutorService buildDefaultBossExecutor()
    {
        return newCachedThreadPool(renamingThreadFactory(threadNamePattern("-boss-%s")));
    }

    private ExecutorService buildDefaultWorkerExecutor()
    {
        return newCachedThreadPool(renamingThreadFactory(threadNamePattern("-worker-%s")));
    }

    private String threadNamePattern(String suffix)
    {
        String niftyName = getSmartName();
        return "nifty-server" + (Strings.isNullOrEmpty(niftyName) ? "" : "-" + niftyName) + suffix;
    }

    private ThreadFactory renamingThreadFactory(String nameFormat)
    {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
    }
}
