package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.clent.config.ClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 13:54
 */
public class NettyClient {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private EventLoopGroup boss;
    private ClientConfig config;
    private ThreadPoolExecutor executor;

    public NettyClient(ClientConfig config) {
        this.config = config;
        boss = new NioEventLoopGroup(getThreadSize(config.getNettyBossThreadSize()), new DefaultThreadFactory("netty-client-boss"));
        executor = new ThreadPoolExecutor(1, getThreadSize(config.getWorkThreadSize()), 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new DefaultThreadFactory("scud-client-work", true), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public Channel connect(String ip, int port) {
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getConnectTimeout())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.group(boss).channel(NioSocketChannel.class).handler(new NettyClientInitializer(executor));
        try {
            return boot.connect(ip, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int getThreadSize(int size) {
        if (size < 1) {
            size = Runtime.getRuntime().availableProcessors();
        }
        return size;
    }
}
