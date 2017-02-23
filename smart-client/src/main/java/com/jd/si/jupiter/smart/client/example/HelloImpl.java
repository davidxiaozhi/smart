package com.jd.si.jupiter.smart.client.example;

import org.apache.thrift.TException;

/**
 * Created with IntelliJ IDEA.
 * User: yuwenhu
 * Date: 14-9-23
 * Time: 下午7:07
 * To change this template use File | Settings | File Templates.
 */
public class HelloImpl implements  Hello.Iface {
    @Override
    public String helloString(String para) throws TException {
        String str = "hello "+para+" 20140923" ;
        System.out.println("server:"+str);
        try {
            //Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}
