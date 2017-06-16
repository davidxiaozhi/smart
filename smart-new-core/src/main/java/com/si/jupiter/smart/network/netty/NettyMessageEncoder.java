package com.si.jupiter.smart.network.netty;

import com.si.jupiter.smart.core.NetworkProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 16:44
 * 协议编码器
 * <p>
 * |----魔数(1) ----|----header长度(4)----|----消息内容长度(4)----|----协议版本(1)----|----序列化类型(1)----|----消息id(4)----|---超时时间(4)----|---请求时间(8)----|
 * <p>
 * |----header消息(m)----|----消息内容(n)----|
 * <p>
 * 传输总长度 27+m+n
 * 解码时必须先提取前23字节进行校验，确定使用接收消息字节数组大小
 */
public class NettyMessageEncoder extends MessageToByteEncoder {
    private final static Logger LOGGER = LoggerFactory.getLogger(NettyMessageEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            super.write(ctx, msg, promise);
        } catch (Exception e) {
            LOGGER.error("write the data error!!! to remote address is {}", ctx.channel().remoteAddress());
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        NetworkProtocol p = (NetworkProtocol) msg;
        byte[] headerBytes = p.getHeader().getHeaderBytes();
        try {
            out.writeByte(p.getMagicNumber());//魔数
            out.writeInt(headerBytes.length);//消息头长度
            int messageLen = headerBytes.length + p.getContent().length;
            out.writeInt(messageLen);////消息总长度(4)(头信息＋内容)
            out.writeByte(p.getProtocolVersion());//版本号
            out.writeByte(p.getSerializeType());//序列化类型
            out.writeInt(p.getSequence());//消息id
            out.writeInt(p.getRequestTimeout());//请求超时时间
            out.writeLong(System.currentTimeMillis());//请求时间
            if (headerBytes.length != 0) {
                out.writeBytes(headerBytes);
            }
            out.writeBytes(p.getContent());
            //out.writeByte(p.getTail());
        } catch (Exception e) {
            LOGGER.error("Encode fail. seq={} , remoteAddress: {}", p.getSequence(), ctx.channel().remoteAddress(), e);
            throw new Exception("Encoding msg fail. seq=" + p.getSequence(), e);
        }
    }
}
