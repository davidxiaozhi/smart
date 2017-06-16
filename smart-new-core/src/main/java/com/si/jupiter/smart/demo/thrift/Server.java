package com.si.jupiter.smart.demo.thrift;


import com.si.jupiter.smart.core.thrift.ProtocolType;
import com.si.jupiter.smart.core.thrift.ServerThriftConfig;
import com.si.jupiter.smart.server.Provider;
import com.si.jupiter.smart.server.ServerConfig;
import com.si.jupiter.smart.server.SmartServer;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 12:08
 */
public class Server {
    public static void main(String[] args) {
        startServer(5050);
    }
    public static void startServer(int port){
        ServerConfig conf = new ServerConfig();
        conf.setThriftConfig(new ServerThriftConfig(Hello.Iface.class,Hello.Processor.class,new HelloImpl(), ProtocolType.TCompactProtocol));
        conf.setPort(port).setCorePoolSize(12);
        Provider<Hello.Iface> provider = new Provider<Hello.Iface>(Hello.Iface.class, new HelloImpl(), "1.0.1");
        SmartServer server = new SmartServer(conf, provider);
        server.start();
    }
}
