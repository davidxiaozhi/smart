package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.core.NetworkProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 16:27
 */
public class IdleEventHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER = LoggerFactory.getLogger(IdleEventHandler.class);
    private final static byte[] heart = "HEART-V1".getBytes();
    private static int count = 0;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                ctx.close();
                LOGGER.error("Client didn't receive the heart message.");
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {

            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                NetworkProtocol protocol = new NetworkProtocol();
                protocol.setSerializeType((byte) -1);
                protocol.setSequence(count++);
                protocol.setContent(heart);
                ctx.writeAndFlush(protocol);
            }
        }
    }
}
