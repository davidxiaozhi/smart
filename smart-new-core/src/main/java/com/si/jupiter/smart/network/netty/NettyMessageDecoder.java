package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.commons.Config;
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
 * |----魔数(1) ----|----header长度(4)----|----消息总长度(4)----|----协议版本(1)----|----序列化类型(1)----|----消息id(4)----|---超时时间(4)----|---请求时间(8)----|
 *
 * |----header消息(m)----|----消息内容(n)----|
 *
 * 传输总长度 27+m+n
 * 解码时必须先提取前27字节进行校验，确定使用接收消息字节数组大小
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyMessageDecoder.class);

    public NettyMessageDecoder() {
        super(1024*1024* Config.getValue("netty.transfer.maxBytes",100),
                5, 4, 27-5-4, 0, false);
    }

    /**
     * 这里重写channelRead主要是为了捕获异常
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            super.channelRead(ctx, msg);
        }catch (Exception e){
            LOGGER.error("read the data error!!! the remote address is {}",ctx.channel().remoteAddress());
        }
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
        if (in.readableBytes() > 27) { //协议最低19字节的信息
            byte magicNum = in.readByte();//魔数(1)
            if (magicNum == NetworkProtocol.MAGIC_NUMBER) {
                NetworkProtocol protocol = new NetworkProtocol();
                int headerLen = in.readInt();//消息头长度(4)
                int messageLen = in.readInt();//消息总长度(4)(头信息＋内容)
                byte version = in.readByte();//协议版本号(1)
                byte type = in.readByte();//序列化类型(1)
                int seq = in.readInt();//消息id(4)
                int requestTimeout = in.readInt();//请求超时时间(4)
                long  requestTime= in.readLong();//请求时间(8)
                if (headerLen > 0) {//传递头信息才会处理头信息
                    byte[] headBytes = new byte[headerLen];
                    in.readBytes(headBytes);
                    protocol.getHeader().setHeaderBytes(headBytes);
                }
                int contentLen = messageLen-headerLen;
                byte[] content = new byte[contentLen];
                in.readBytes(content);
                protocol.setProtocolVersion(version);
                protocol.setSerializeType(type);
                protocol.setSequence(seq);
                protocol.setRequestTimeout(requestTimeout);
                protocol.setContent(content);
                protocol.setRequestTime(requestTime);
                return protocol;
            } else {
                in.clear();
                LOGGER.error("Protocol head parsing error, head={}", magicNum);
            }
        }
        return null;
    }
}
