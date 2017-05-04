package com.si.jupiter.smart.client.example;

import com.google.common.net.HostAndPort;
import com.si.jupiter.smart.client.channel.FramedClientChannel;
import com.si.jupiter.smart.client.config.NettyClientConfig;
import com.si.jupiter.smart.client.config.NettyClientConfigBuilder;
import com.si.jupiter.smart.client.config.ThriftClientDef;
import com.si.jupiter.smart.client.config.ThriftClientDefBuilder;
import com.si.jupiter.smart.client.old.SmartClientChannelInitializer;
import com.si.jupiter.smart.client.transport.TSmartClientChannelTransport;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.si.jupiter.smart.handler.DebugHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.net.InetSocketAddress;

/**
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  17-5-3 上午10:31
 * Last Update:  17-5-3 上午10:31
 */
public class NettyClientDemo {
    public static void main(String[] args) {
        //使用netty原生channelPool
        //构建Client
        NioEventLoopGroup group = new NioEventLoopGroup();
        final Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true);
                /*.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {

                        //p.addLast(new EchoClientHandler());
                    }
                });*/

        HostAndPort hostAndPort = HostAndPort.fromParts("127.0.0.1",9090);
        ChannelPoolMap<InetSocketAddress, SimpleChannelPool> poolMap = new AbstractChannelPoolMap<InetSocketAddress, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(InetSocketAddress key) {
                return new SimpleChannelPool(
                        b.remoteAddress(key),
                        new ChannelPoolHandler() {
                    @Override
                    public void channelReleased(Channel ch) throws Exception {
                        System.out.println("=channelReleased= host:"+ch.remoteAddress());
                    }

                    @Override
                    public void channelAcquired(Channel ch) throws Exception {
                        System.out.println("=channelAcquired= host:"+ch.remoteAddress());
                    }

                    @Override
                    public void channelCreated(Channel ch) throws Exception {
                        System.out.println("=channelCreated= host:"+ch.remoteAddress());
                    }
                });
            }
        };


        //netty相关配置
        NettyClientConfigBuilder configBuilder = new NettyClientConfigBuilder();
        configBuilder.setDefaultSocksProxyAddress(hostAndPort);
        configBuilder.setBossThreadCount(1);
        configBuilder.setBossThreadCount(8);
        configBuilder.setSmartName("netty-client");
        NettyClientConfig config = configBuilder.build();
        //thrift　相关配置
        ThriftClientDefBuilder builder = ThriftClientDef.newBuilder().name("nettyThriftClient")
                .protocol(TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory()).getInputProtocolFactory())
                .withProcessor(new Hello.Processor(new HelloImpl()));
        ThriftClientDef thriftClientDef = builder.build();

        //使用channelPool
        final SimpleChannelPool pool = poolMap.get(new InetSocketAddress(hostAndPort.getHostText(),hostAndPort.getPort()));
        Future<Channel> f = pool.acquire();
        f.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> f) {
                if (f.isSuccess()) {
                    Channel ch = f.getNow();
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new DebugHandler("client-start1",true));
                    p.addLast(new ChannelDuplexHandler(){
                        @Override
                        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                            super.write(ctx, msg, promise);
                            System.out.println("========flush===========");
                            ctx.flush();
                        }

                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("========channelActive===========");
                            super.channelActive(ctx);
                        }

                        @Override
                        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                            System.out.println("========channelInActive===========");
                            super.channelInactive(ctx);
                        }

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            System.out.println("========channelRead===========");
                            super.channelRead(ctx, msg);
                        }
                    });
                    p.addLast(new DebugHandler("client-start2",true));

                    // Do somethings
                    // ...
                    // ...
                    System.out.println("Do somethings　start");
                    ch.pipeline().addLast(new DebugHandler("client-start-inner",true));

                    //依据thrift　配置构建thrift　client
                    TDuplexProtocolFactory duplexProtocolFactory =  TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory(true,true));

                    TSmartClientChannelTransport clientTransport = new TSmartClientChannelTransport(
                            SiHelloClient.class,new FramedClientChannel(ch, new HashedWheelTimer(),duplexProtocolFactory));
                    //SIface iface = new SiHelloClient(duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport)
                    //       ,duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport));
                    SiHelloClient iface = new SiHelloClient(duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport)
                            ,duplexProtocolFactory.getInputProtocolFactory().getProtocol(clientTransport));
                    Future<String> future = null;
                    try {
                        //future=iface.helloString("come on baby");
                        //System.out.println(future.getNow());
                        String result = iface.helloString2("come on baby");
                        System.out.println("===================="+result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println("Do somethings　end");
                    // Release back to pool
                    pool.release(ch);
                }
            }
        });

    }
}
