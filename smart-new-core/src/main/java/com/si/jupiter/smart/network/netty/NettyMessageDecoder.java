package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.core.NetworkProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 16:49
 * 协议解码器
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyMessageDecoder.class);

    public NettyMessageDecoder() {
        super(Integer.MAX_VALUE, 7, 4, 0, 0,false);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (frame != null) {
                return this.decode(frame, ctx);
            }
        } catch (Exception t) {
            LOGGER.error("Decoding msg fail! remoteAddress:{}", ctx.channel().remoteAddress(), t);
            throw t;
        } finally {
            if (frame != null) {
                ReferenceCountUtil.release(frame);
            }
        }
        return null;
    }

    protected NetworkProtocol decode(ByteBuf in, ChannelHandlerContext ctx) throws Exception {
        if (in.readableBytes() > 0) {
            byte head = in.readByte();
            if (head == 124) {
                byte version = in.readByte();
                byte type = in.readByte();
                int seq = in.readInt();
                int len = in.readInt();
                byte[] content = new byte[len];
                in.readBytes(content);
                NetworkProtocol protocol = new NetworkProtocol();
                protocol.setVersion(version);
                protocol.setType(type);
                protocol.setSequence(seq);
                protocol.setContent(content);
                return protocol;
            } else {
                in.clear();
                LOGGER.error("Protocol head parsing error, head={}", head);
            }
        }
        return null;
    }
}
