package com.si.jupiter.smart.network;


import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcInvocation;
import com.si.jupiter.smart.core.RpcResult;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 12:32
 */
public class SerializableHandler {
    public static RpcInvocation requestDecode(NetworkProtocol protocol) {
        RpcSerializable<RpcInvocation> ser = SerializableHandler.getSerializable(protocol.getType());
        return ser.decode(protocol.getContent(), RpcInvocation.class);
    }

    public static byte[] requestEncode(byte type, RpcInvocation invocation) {
        RpcSerializable ser = SerializableHandler.getSerializable(type);
        return ser.encode(invocation);
    }

    public static RpcResult responseDecode(NetworkProtocol protocol) {
        RpcSerializable<RpcResult> ser = SerializableHandler.getSerializable(protocol.getType());
        return ser.decode(protocol.getContent(), RpcResult.class);
    }

    public static byte[] responseEncode(byte type, RpcResult result) {
        RpcSerializable ser = SerializableHandler.getSerializable(type);
        return ser.encode(result);
    }

    private static RpcSerializable getSerializable(byte type) {
        if (type == SerializableEnum.PROTOBUF.getValue()) {
            return ProtobufSerializable.newInstance();
        } else if (type == SerializableEnum.KRYO.getValue()) {
            return KryoSerializable.newInstance();
        }
        return null;
    }
}
