package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.core.NetworkProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 16:44
 * 协议编码器
 */
public class NettyMessageEncoder extends MessageToByteEncoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        NetworkProtocol p = (NetworkProtocol) msg;
        try {
            out.writeByte(p.getHead());//head
            out.writeByte(p.getVersion());//version
            out.writeByte(p.getType());//type
            out.writeInt(p.getSequence());//seq
            out.writeInt(p.getContent().length);//len
            out.writeBytes(p.getContent());
            //out.writeByte(p.getTail());
        } catch (Exception e) {
            LOGGER.error("Encode fail. seq={} , remoteAddress: {}", p.getSequence(), ctx.channel().remoteAddress(), e);
            throw new Exception("Encoding msg fail. seq=" + p.getSequence(), e);
        }
    }
}
