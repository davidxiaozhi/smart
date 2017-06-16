package com.si.jupiter.smart.core.thrift;

public enum ProtocolType{
        TBinaryProtocol((byte) 1),TCompactProtocol((byte) 2);
        byte value;

        ProtocolType(byte v) {
            this.value = v;
        }

        public byte getValue() {
            return value;
        }
    }