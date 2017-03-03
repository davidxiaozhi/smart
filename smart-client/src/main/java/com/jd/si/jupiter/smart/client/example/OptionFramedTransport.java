package com.jd.si.jupiter.smart.client.example;

import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  17-3-2 上午10:41
 * Last Update:  17-3-2 上午10:41
 */
public class OptionFramedTransport extends TFramedTransport {
    public OptionFramedTransport(TTransport transport, int maxLength) {
        super(transport, maxLength);
    }
}
