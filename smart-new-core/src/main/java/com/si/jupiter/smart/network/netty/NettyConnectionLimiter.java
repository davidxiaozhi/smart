package com.si.jupiter.smart.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class NettyConnectionLimiter extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(NettyConnectionLimiter.class);
    private final AtomicInteger numConnections;
    private final int maxConnections;//单个实例最大链接数目

    public NettyConnectionLimiter(int maxConnections) {
        this.maxConnections = maxConnections;
        this.numConnections = new AtomicInteger(0);
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        //超过最大数目值是关闭
        if (maxConnections > 0 && numConnections.incrementAndGet() > maxConnections) {
            ctx.channel().close();
            // numConnections will be decremented in channelClosed
            logger.info("Accepted connection above limit {}. Dropping.", maxConnections);
        }
        else{
            logger.debug("the server current connection nums is {}",numConnections.get());
        }
        super.channelRegistered(ctx);
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (maxConnections > 0 && numConnections.decrementAndGet() < 0) {
            logger.info("BUG in NettyConnectionLimiter!!!!!");
        }
        super.channelUnregistered(ctx);
    }



}
