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


import com.jd.si.jupiter.smart.handler.SmartChannelInitializer;
import com.jd.si.jupiter.smart.statistics.ChannelStatistics;
import com.jd.si.jupiter.smart.statistics.SmartMetrics;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * A   channel the decode framed Thrift message, dispatches to the TProcessor given
 * and then encode message back to Thrift frame.
 * 一个channel用来解码 thrift消息窗口,并分发给传递给他的TProcessor,同时编码thrift消息窗口传递过来的消息
 * NettyServerTransport 负责维护一套ServerBootStrap
 */
public class NettyServerTransport {
    //private static final Logger log = Logger.get(NettyServerTransport.class);

    private final int requestedPort;
    private int actualPort;
    private final ChannelInitializer channelInitializer;
    private static final long NO_WRITER_IDLE_TIMEOUT = 0;
    private static final long NO_ALL_IDLE_TIMEOUT = 0;
    private ServerBootstrap bootstrap;
    private final ChannelGroup allChannels;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private final ThriftServerDef def;
    private final NettyServerConfig nettyServerConfig;
    private final ChannelStatistics channelStatistics;

    public NettyServerTransport(
            final ThriftServerDef def,
            final NettyServerConfig nettyServerConfig,
            final ChannelGroup allChannels) {
        this.def = def;
        this.nettyServerConfig = nettyServerConfig;
        this.requestedPort = def.getServerPort();
        this.allChannels = allChannels;
        //负责整个服务端的指标统计
        this.channelStatistics = new ChannelStatistics();
        this.channelInitializer = new SmartChannelInitializer(def,nettyServerConfig);
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(nettyServerConfig.getBossThreadCount());
        if (nettyServerConfig.getWorkerThreadCount() <= 0) {
            workerGroup = new NioEventLoopGroup();//不限数目
        } else {
            workerGroup = new NioEventLoopGroup(nettyServerConfig.getWorkerThreadCount());
        }

        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(channelInitializer);

        // Start the server.
        ChannelFuture f = bootstrap.bind(requestedPort).sync();

        // Wait until the server socket is closed.
        f.channel().closeFuture().sync();

    }

    public void stop()
            throws InterruptedException {
        // Shut down all event loops to terminate all threads.
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public int getPort() {
        if (actualPort != 0) {
            return actualPort;
        } else {
            return requestedPort; // may be 0 if server not yet started
        }
    }

    public SmartMetrics getMetrics() {
        return channelStatistics;
    }
}
