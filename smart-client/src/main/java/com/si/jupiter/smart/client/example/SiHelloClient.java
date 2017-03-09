package com.si.jupiter.smart.client.example;

import com.si.jupiter.smart.client.SmartClient;
import io.netty.util.concurrent.Future;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

public class SiHelloClient extends TServiceClient implements SIface
{
    private SmartClient smartClient;

    public SiHelloClient(TProtocol iprot, TProtocol oprot) {
        super(iprot, oprot);
    }

    @Override
    public Future<String> helloString(String para) throws TException {
        send_helloString(para);
        return null;
    }

    public void send_helloString(String para) throws TException
    {
        Hello.helloString_args args = new Hello.helloString_args();
        args.setPara(para);
        sendBase("helloString", args);
    }

    public String recv_helloString() throws TException
    {
        Hello.helloString_result result = new Hello.helloString_result();
        receiveBase(result, "helloString");
        if (result.isSetSuccess()) {
            return result.success;
        }
        throw new org.apache.thrift.TApplicationException(org.apache.thrift.TApplicationException.MISSING_RESULT, "helloString failed: unknown result");
    }
}
