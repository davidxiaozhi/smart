package com.si.jupiter.smart.core.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-15 下午4:51
 * Last Update:  17-6-15 下午4:51
 */
public class ThriftUtils {
    public static TProtocol getProtocolByType(ProtocolType protocolType, final ByteArrayInputStream baos_) {
        TIOStreamTransport transport_ = new TIOStreamTransport(baos_);
        if (ProtocolType.TCompactProtocol.equals(protocolType)) {
            return new TCompactProtocol.Factory().getProtocol(transport_);
        }
        return new TBinaryProtocol.Factory().getProtocol(transport_);
    }
    public static TProtocol getProtocolByType(ProtocolType protocolType, final ByteArrayOutputStream baos_) {
        TIOStreamTransport transport_ = new TIOStreamTransport(baos_);
        if (ProtocolType.TCompactProtocol.equals(protocolType)) {
            return new TCompactProtocol.Factory().getProtocol(transport_);
        }
        return new TBinaryProtocol.Factory().getProtocol(transport_);
    }
}
