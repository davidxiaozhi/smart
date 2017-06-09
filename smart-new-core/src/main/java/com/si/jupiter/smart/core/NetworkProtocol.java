package com.si.jupiter.smart.core;

import java.io.Serializable;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 17:08
 * 网络协议
 * 2017-0509 协议定义
 * 占用字节 　内容
 * 1　　　　　协议魔数
 * 2-5(4)   消息总长度(m)
 * 6-9(4)　　　消息header长度(n)
 * 10-(10+n)  n字节的header
 * (10+n+1)-(10+n+1+m) m字节的消息体
 */
public class NetworkProtocol implements Serializable {
    public final static byte MAGIC_NUMBER = (byte) 89;//协议头
    private ProtocolHeader header;
    private byte protocolVersion =1;//协议版本
    private byte serializeType =1;//序列化方式
    private int sequence;//包序号
    private int len;//内容长度
    private byte[] content;//包内容
    private byte tail = (byte) 126;//协议尾

    public ProtocolHeader getHeader() {
        return header;
    }

    public void setHeader(ProtocolHeader header) {
        this.header = header;
    }

    public byte getMagicNumber() {
        return MAGIC_NUMBER;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public byte getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(byte serializeType) {
        this.serializeType = serializeType;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public byte getTail() {
        return tail;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
