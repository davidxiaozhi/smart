package com.si.jupiter.smart.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class FlushHandler extends ChannelDuplexHandler{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("========flush  writeAndFlush===========");
        ctx.writeAndFlush(msg, promise);
    }
}
