package com.si.jupiter.smart.demo;


import com.si.jupiter.smart.demo.idl.Test;
import com.si.jupiter.smart.demo.idl.TestImpl;
import com.si.jupiter.smart.server.Provider;
import com.si.jupiter.smart.server.ScudServer;
import com.si.jupiter.smart.server.ServerConfig;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 12:08
 */
public class Server {
    public static void main(String[] args) {
        ServerConfig conf = new ServerConfig();
        conf.setPort(7890).setCorePoolSize(12);
        Provider<Test> provider = new Provider<Test>(Test.class, new TestImpl(), "1.0.1");
        ScudServer server = new ScudServer(conf, provider);
        server.start();
    }
}
