package com.si.jupiter.smart.client.old;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.si.jupiter.smart.client.config.NettyClientConfig;
import com.si.jupiter.smart.core.SmartChannelOption;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import java.util.concurrent.ThreadFactory;

public class SmartClientBootstrap {
    private final NettyClientConfig nettyClientConfig;//客户端bootstrap配置信息
    private final Bootstrap clientBootstrap = new Bootstrap();
    private final EventLoopGroup executor;

    public SmartClientBootstrap(NettyClientConfig nettyClientConfig) {
        this.nettyClientConfig = nettyClientConfig;
        this.executor = buildDefaultWorkerExecutor();
        this.clientBootstrap.group(executor)
                .channel(NioSocketChannel.class);
        //批处理配置网络属性
        for (SmartChannelOption channelOption: nettyClientConfig.getBootstrapOptions()) {
            this.clientBootstrap.option(channelOption.getOption(),channelOption.getValue());
        }
        ///this.clientBootstrap.handler(nettyClientConfig.getChannelInitializer());
    }

    public void connectSync(String host,int port) throws InterruptedException {
        ChannelFuture f = this.clientBootstrap.connect(host, port).sync();
    }

    private EventLoopGroup buildDefaultWorkerExecutor() {
        String smartName = nettyClientConfig.getSmartName();//线程池独立名字
        String threadPoolName = "smart-client" + (Strings.isNullOrEmpty(smartName) ? "" : "-" + smartName);
        return new NioEventLoopGroup(nettyClientConfig.getThreadCount(), new DefaultThreadFactory(threadPoolName,true));
    }

    private String threadNamePattern(String suffix) {
        String smartName = nettyClientConfig.getSmartName();
        return "smart-client" + (Strings.isNullOrEmpty(smartName) ? "" : "-" + smartName) + suffix;
    }

    private ThreadFactory renamingDaemonThreadFactory(String nameFormat) {
        return new ThreadFactoryBuilder().setNameFormat(nameFormat).setDaemon(true).build();
    }


}
