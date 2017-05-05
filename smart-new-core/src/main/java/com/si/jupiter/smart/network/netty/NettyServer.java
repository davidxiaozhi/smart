package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.server.ServerConfig;
import com.si.jupiter.smart.server.ServerManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 12:32
 */
public class NettyServer {
    private static NioEventLoopGroup bossGroup;
    private static NioEventLoopGroup workerGroup;
    private static ChannelFuture future;

    public static void start(ServerConfig config, ServerManager manager) {
        bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-server-boss"));
        workerGroup = new NioEventLoopGroup(config.getNettyWorkPooleSize(), new DefaultThreadFactory("netty-server-work", true));
        ServerBootstrap boot = new ServerBootstrap();
        boot.group(bossGroup, workerGroup)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnentTimeout())
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 65536))
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        boot.childHandler(new NettyServerInitializer(manager));
        boot.channel(NioServerSocketChannel.class);
        future = boot.bind(config.getPort()).syncUninterruptibly();
        System.out.println("Server start sucess.");
        System.out.println("************************************************************************************************");
        System.out.println("*	██████╗  █████╗ ██╗   ██╗██╗██████╗ ██╗  ██╗██╗ █████╗  ██████╗ ███████╗██╗  ██╗██╗		   *");
        System.out.println("*	██╔══██╗██╔══██╗██║   ██║██║██╔══██╗╚██╗██╔╝██║██╔══██╗██╔═══██╗╚══███╔╝██║  ██║██║		   *");
        System.out.println("*	██║  ██║███████║██║   ██║██║██║  ██║ ╚███╔╝ ██║███████║██║   ██║  ███╔╝ ███████║██║		   *");
        System.out.println("*	██║  ██║██╔══██║╚██╗ ██╔╝██║██║  ██║ ██╔██╗ ██║██╔══██║██║   ██║ ███╔╝  ██╔══██║██║		   *");
        System.out.println("*	██████╔╝██║  ██║ ╚████╔╝ ██║██████╔╝██╔╝ ██╗██║██║  ██║╚██████╔╝███████╗██║  ██║██║		   *");
        System.out.println("*	╚═════╝ ╚═╝  ╚═╝  ╚═══╝  ╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝╚═╝	v1.0.0 *");
        System.out.println("***********************************************************************************************");
    }

    public static void stop() {
        future.channel().closeFuture().syncUninterruptibly();
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
    }
}
