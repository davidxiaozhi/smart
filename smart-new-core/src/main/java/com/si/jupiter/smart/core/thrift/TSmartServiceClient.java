package com.si.jupiter.smart.core.thrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TMessageType;
import org.apache.thrift.protocol.TProtocol;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-16 下午4:22
 * Last Update:  17-6-16 下午4:22
 */
public class TSmartServiceClient extends TServiceClient {
    public TSmartServiceClient(TProtocol prot) {
        super(prot);
    }

    public TSmartServiceClient(TProtocol iprot, TProtocol oprot) {
        super(iprot, oprot);
    }

    @Override
    protected void receiveBase(TBase<?, ?> result, String methodName) throws TException {
        TMessage msg = iprot_.readMessageBegin();
        if (msg.type == TMessageType.EXCEPTION) {
            TApplicationException x = TApplicationException.read(iprot_);
            iprot_.readMessageEnd();
            throw x;
        }
        /*if (msg.seqid != seqid_) {
            throw new TApplicationException(TApplicationException.BAD_SEQUENCE_ID, methodName + " failed: out of sequence response");
        }*/
        result.read(iprot_);
        iprot_.readMessageEnd();
    }
}
