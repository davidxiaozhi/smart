package com.jd.si.jupiter.smart.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  17-3-1 下午8:01
 * Last Update:  17-3-1 下午8:01
 */
public class FlushHandler extends ChannelDuplexHandler{
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("========flush  writeAndFlush===========");
        ctx.writeAndFlush(msg, promise);
    }
}
