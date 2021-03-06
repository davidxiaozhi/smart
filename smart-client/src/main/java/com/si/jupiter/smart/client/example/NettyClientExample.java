package com.si.jupiter.smart.client.example;


import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.si.jupiter.smart.client.SmartClient;
import com.si.jupiter.smart.client.channel.FramedClientChannel;
import com.si.jupiter.smart.client.channel.SmartClientChannel;
import com.si.jupiter.smart.client.config.NettyClientConfig;
import com.si.jupiter.smart.client.config.NettyClientConfigBuilder;
import com.si.jupiter.smart.client.connector.FramedClientConnector;
import com.si.jupiter.smart.client.connector.SmartClientConnector;
import com.si.jupiter.smart.client.transport.TSmartClientChannelTransport;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.concurrent.ExecutionException;

public class NettyClientExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //构建netty
        NettyClientConfigBuilder configBuilder = new NettyClientConfigBuilder();
        configBuilder.setDefaultSocksProxyAddress(HostAndPort.fromParts("127.0.0.1",9090));
        configBuilder.setBossThreadCount(1);
        configBuilder.setBossThreadCount(8);
        configBuilder.setSmartName("netty-client");
        NettyClientConfig config = configBuilder.build();
        //构建SmartClient为了获得netty的channel
        SmartClient client = new SmartClient(config);
        SmartClientConnector connector = new FramedClientConnector(config.getDefaultSocksProxyAddress());
        ListenableFuture<SmartClientChannel> channelFuture = client.connectAsync(connector);
        //channelFuture.get().getNettyChannel().writeAndFlush("");
        //================================================
        //协议工厂负责序列化及反序列化
        TDuplexProtocolFactory duplexProtocolFactory =  TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory(true,true));
        //netty的channel构造transport为了transport的信息写入
        TSmartClientChannelTransport clientTransport = new TSmartClientChannelTransport(null,channelFuture.get());
        //开始服务调用
        SIface iface = new SiHelloClient(duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport)
                ,duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport));
        Future<String> future = null;
        try {
            future=iface.helloString("come on baby");
            System.out.println(future.getNow());
        } catch (TException e) {
            e.printStackTrace();
        }
        ByteBuf output = clientTransport.getRequestBufferTransport().getOutputBuffer();
        DefaultChannelPromise promise =new DefaultChannelPromise(channelFuture.get().getNettyChannel());
        channelFuture.get().getNettyChannel().writeAndFlush(output).get();
    }
}
