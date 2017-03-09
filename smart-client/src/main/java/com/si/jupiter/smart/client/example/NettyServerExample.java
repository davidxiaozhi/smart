package com.si.jupiter.smart.client.example;

import com.si.jupiter.smart.SmartBootstrap;
import com.si.jupiter.smart.core.NettyServerConfig;
import com.si.jupiter.smart.core.NettyServerConfigBuilder;
import com.si.jupiter.smart.core.ThriftServerDef;
import com.si.jupiter.smart.core.ThriftServerDefBuilder;
import com.si.jupiter.smart.handler.SmartChannelInitializer;
import com.si.jupiter.smart.processor.SmartProcessorAdapters;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashSet;
import java.util.Set;


public class NettyServerExample {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static void main(String[] args) {
        ThriftServerDefBuilder builder = new ThriftServerDefBuilder();
        builder.listen(9090)
                .name("TestNettyServer")
                .withProcessorFactory(SmartProcessorAdapters.factoryFromTProcessor(new Hello.Processor(new HelloImpl())));
        ThriftServerDef thriftServerDef = builder.build();
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
