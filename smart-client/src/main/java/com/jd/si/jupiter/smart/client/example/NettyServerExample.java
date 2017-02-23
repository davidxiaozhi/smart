package com.jd.si.jupiter.smart.client.example;

import com.jd.si.jupiter.smart.SmartBootstrap;
import com.jd.si.jupiter.smart.core.*;
import com.jd.si.jupiter.smart.handler.SmartChannelInitializer;
import com.jd.si.jupiter.smart.processor.SmartProcessorAdapters;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashSet;
import java.util.Set;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  17-2-16 下午1:46
 * Last Update:  17-2-16 下午1:46
 */
public class NettyServerExample {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static void main(String[] args) {
        ThriftServerDefBuilder builder = new ThriftServerDefBuilder();
        builder.listen(9090)
                .name("TestNettyServer")
                .withProcessorFactory(SmartProcessorAdapters.factoryFromTProcessor(new Hello.Processor(new HelloImpl())));
        ThriftServerDef thriftServerDef = builder.build();
        //NettyClientConfig nettyClientConfig = new NettyClientConfig();
        NettyServerConfigBuilder serverConfigbuilder = new NettyServerConfigBuilder();
        NettyServerConfig nettyServerConfig = serverConfigbuilder.setSmartName("TestSmartServer")
                .setBossThreadCount(1)
                .setWorkerThreadCount(10)
                .build();
        ChannelInitializer initializer = new SmartChannelInitializer(thriftServerDef,nettyServerConfig);
        Set<ThriftServerDef> defSet = new HashSet<ThriftServerDef>();
        defSet.add(thriftServerDef);
        SmartBootstrap serverBootstrap = new SmartBootstrap(defSet, nettyServerConfig,channels);
        try {
            serverBootstrap.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
            serverBootstrap.stop();
        }
    }
}