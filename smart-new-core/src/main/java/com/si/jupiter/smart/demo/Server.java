package com.si.jupiter.smart.demo;


import com.si.jupiter.smart.demo.idl.Test;
import com.si.jupiter.smart.demo.idl.TestImpl;
import com.si.jupiter.smart.server.Provider;
import com.si.jupiter.smart.server.SmartServer;
import com.si.jupiter.smart.server.ServerConfig;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 12:08
 */
public class Server {
    public static void main(String[] args) {
        startServer(5050);
        startServer(5051);
        startServer(5052);
    }
    public static void startServer(int port){
        ServerConfig conf = new ServerConfig();
        conf.setPort(port).setCorePoolSize(12);
        Provider<Test> provider = new Provider<Test>(Test.class, new TestImpl(), "1.0.1");
        SmartServer server = new SmartServer(conf, provider);
        server.start();
    }
}
