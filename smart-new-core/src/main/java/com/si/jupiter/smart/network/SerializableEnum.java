package com.si.jupiter.smart.network;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 12:07
 */
public enum SerializableEnum {
    PROTOBUF((byte) 1), KRYO((byte) 2);
    byte value;

    SerializableEnum(byte v) {
        this.value = v;
    }

    public byte getValue() {
        return value;
    }
}
