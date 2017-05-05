package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.commons.Config;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 14:32
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    private ThreadPoolExecutor executor;

    public NettyClientInitializer(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        int idel = Config.getValue("netty.idle.time", 3000);

        pipeline.addLast("decoder", new NettyMessageDecoder());
        pipeline.addLast("encoder", new NettyMessageEncoder());

        pipeline.addLast("idleState", new IdleStateHandler(idel * 3, idel * 3, idel, TimeUnit.MILLISECONDS));
        pipeline.addLast("idleEvent", new IdleEventHandler());

        pipeline.addLast("handler", new NettyClientHandler(this.executor));
    }
}
