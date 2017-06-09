package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.server.ServiceInvokeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 14:25
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<NetworkProtocol> {
    private ServiceInvokeManager manager;

    public NettyServerHandler(ServiceInvokeManager manager) {
        this.manager = manager;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NetworkProtocol msg) throws Exception {
        if (msg.getSerializeType() == -1) {//心跳包
            ctx.writeAndFlush(msg);
        } else {
            manager.invoke(msg, ctx);
        }
    }
}
