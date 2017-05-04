package com.si.jupiter.smart.client.example;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  17-2-16 下午6:19
 * Last Update:  17-2-16 下午6:19
 */

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThriftClient {
    public static void main(String[] args) {
        sync(true,5);
        //moreThread();
    }
    private  static void moreThread(){
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 100; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    sync(true,100);
                }
            });
        }
        service.shutdown();
    }
    private static void sync(boolean isFrame,int times) {
        try {
            TSocket transport = new TSocket("localhost", 9090);
            transport.getSocket().setSoTimeout(1000);
            TProtocol protocol;
            if(isFrame){
                OptionFramedTransport framedTransport = new OptionFramedTransport(transport,1024);
                framedTransport.open();
                protocol = new TBinaryProtocol(framedTransport,true,true);
            }
            else{
                transport.open();
                protocol = new TBinaryProtocol(transport,true,true);
            }
            Hello.Client client = new Hello.Client(protocol);

            perform(client,times);

            transport.close();
        } catch (TException te) {
            te.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static void perform(Hello.Client client,int times) throws TException {
        for (int i = 0; i < times ; i++) {
            try {
                System.out.println("第"+i+"次调用开始");
               String result = client.helloString(""+i);
                System.out.println("第"+i+"次调用结束 result="+result);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
