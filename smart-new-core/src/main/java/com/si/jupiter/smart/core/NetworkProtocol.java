package com.si.jupiter.smart.core;

import java.io.Serializable;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 17:08
 * 网络协议
 * 2017-0509 协议定义
 */
public class NetworkProtocol implements Serializable {
    public final static byte MAGIC_NUMBER = (byte) 89;//协议头
    private final ProtocolHeader header = new ProtocolHeader();
    private byte protocolVersion =1;//协议版本
    private byte serializeType =1;//序列化方式
    private int sequence;//包序号
    private int requestTimeout;//请求超时时间
    private long requestTime;//请求时间
    private byte[] content;//包内容
    private byte tail = (byte) 111;//协议尾

    public ProtocolHeader getHeader() {
        return header;
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

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public void setTail(byte tail) {
        this.tail = tail;
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

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }
}
