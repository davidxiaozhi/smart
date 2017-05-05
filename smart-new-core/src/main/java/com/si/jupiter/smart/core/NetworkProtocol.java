package com.si.jupiter.smart.core;

import java.io.Serializable;

/**
 * Author: lizhipeng
 * Date: 2017/01/02 17:08
 * 网络协议
 */
public class NetworkProtocol implements Serializable {
    private byte head = (byte) 124;//协议头
    private byte version=1;//协议版本
    private byte type=1;//序列化方式
    private int sequence;//包序号
    private int len;//内容长度
    private byte[] content;//包内容
    private byte tail = (byte) 126;//协议尾


    public byte getHead() {
        return head;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
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
