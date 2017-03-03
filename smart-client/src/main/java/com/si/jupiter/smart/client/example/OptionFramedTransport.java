package com.si.jupiter.smart.client.example;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;


public class OptionFramedTransport extends TFramedTransport {
    public OptionFramedTransport(TTransport transport, int maxLength) {
        super(transport, maxLength);
    }
}
