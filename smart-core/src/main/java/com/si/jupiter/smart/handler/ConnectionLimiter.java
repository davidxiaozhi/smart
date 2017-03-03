package com.si.jupiter.smart.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionLimiter extends ChannelInboundHandlerAdapter {
    private final AtomicInteger numConnections;
    private final int maxConnections;

    public ConnectionLimiter(int maxConnections) {
        this.maxConnections = maxConnections;
        this.numConnections = new AtomicInteger(0);
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //超过最大数目值是关闭
        if (maxConnections > 0 && numConnections.incrementAndGet() > maxConnections) {
            ctx.channel().close();
            // numConnections will be decremented in channelClosed
            System.out.println("Accepted connection above limit " + maxConnections + ". Dropping.");
            //log.info("Accepted connection above limit (%s). Dropping.", maxConnections);
        }
        System.out.println("Accepted connection nums: " + numConnections.get()+" maxConnections: "+maxConnections);
        super.channelRegistered(ctx);
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (maxConnections > 0 && numConnections.decrementAndGet() < 0) {
            System.out.println("BUG in ConnectionLimiter!!!!!");
        }
        super.channelUnregistered(ctx);
    }



}
