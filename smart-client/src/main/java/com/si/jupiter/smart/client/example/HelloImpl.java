package com.si.jupiter.smart.client.example;

import org.apache.thrift.TException;

public class HelloImpl implements  Hello.Iface {
    @Override
    public String helloString(String para) throws TException {
        String str = "hello "+para+" 20140923" ;
        System.out.println("server:"+str);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
