package com.si.jupiter.smart.demo.thrift;

import org.apache.thrift.TException;

public class HelloImpl implements  Hello.Iface {
    @Override
    public String helloString(String para) throws TException {
        String str = "hello "+para+" 20140923" ;
        System.out.println("start-helloString server:"+str);
        /*try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        System.out.println("end-helloString");
        return str;
    }
}
