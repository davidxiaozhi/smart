package com.si.jupiter.smart.network.netty;


import com.si.jupiter.smart.core.MessageManager;
import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcResult;
import com.si.jupiter.smart.network.SerializableHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 14:35
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<NetworkProtocol> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);
    private ThreadPoolExecutor executor;

    public NettyClientHandler(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final NetworkProtocol msg) throws Exception {
        if (msg.getType() == -1) {
            LOGGER.debug("Client recv heart package id={}", msg.getSequence());
        } else {
            this.executor.submit(new Runnable() {
                public void run() {
                    try {
                        RpcResult result = SerializableHandler.responseDecode(msg);
                        MessageManager.release(msg.getSequence(), result);
                    } catch (Exception e) {
                        LOGGER.error("Client handler fail.", e);
                    }
                }
            });
        }
    }

}
