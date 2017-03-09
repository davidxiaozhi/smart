package com.si.jupiter.smart.client.example;

import io.netty.util.concurrent.Future;
import org.apache.thrift.TException;

public interface SIface {
    public Future<String> helloString(String para) throws TException;
}