package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.commons.Config;
import com.si.jupiter.smart.server.ServiceInvokeManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 14:22
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceInvokeManager manager;

    public NettyServerInitializer(ServiceInvokeManager manager) {
        this.manager = manager;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //最大链接管理
        int maxConnectionNums = Integer.valueOf(Config.getValue("",100));
        pipeline.addLast("connection_manager",new NettyConnectionLimiter(maxConnectionNums));
        // 字符串解码 和 编码
        pipeline.addLast("decoder", new NettyMessageDecoder());
        pipeline.addLast("encoder", new NettyMessageEncoder());
        // 自己的逻辑Handler
        pipeline.addLast("handler", new NettyServerHandler(manager));
    }
}
