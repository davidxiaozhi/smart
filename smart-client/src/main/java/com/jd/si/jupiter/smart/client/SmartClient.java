package com.jd.si.jupiter.smart.client;

import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.jd.si.jupiter.smart.client.channel.SmartClientChannel;
import com.jd.si.jupiter.smart.client.config.NettyClientConfig;
import com.jd.si.jupiter.smart.client.config.ThriftClientDef;
import com.jd.si.jupiter.smart.client.config.ThriftClientDefBuilder;
import com.jd.si.jupiter.smart.client.connector.NiftyClientConnector;
import com.jd.si.jupiter.smart.client.example.Hello;
import com.jd.si.jupiter.smart.client.example.HelloImpl;
import com.jd.si.jupiter.smart.client.old.SmartClientChannelInitializer;
import com.jd.si.jupiter.smart.core.SmartChannelOption;
import com.jd.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.jd.si.jupiter.smart.processor.SmartProcessorAdapters;
import com.jd.si.jupiter.smart.utils.Duration;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  16-11-8 下午7:19
 * Last Update:  16-11-8 下午7:19
 */
public class SmartClient {
    public static final Duration DEFAULT_CONNECT_TIMEOUT = new Duration(2, TimeUnit.SECONDS);;//默认创建链接超时时间
    public static final Duration DEFAULT_RECEIVE_TIMEOUT = new Duration(2, TimeUnit.SECONDS);;//默认发送超时时间
    public static final Duration DEFAULT_READ_TIMEOUT = new Duration(2, TimeUnit.SECONDS);;//默认读超时时间
    private static final Duration DEFAULT_SEND_TIMEOUT = new Duration(2, TimeUnit.SECONDS);;//默认创建链接超时时间

    private static final int DEFAULT_MAX_FRAME_SIZE = 16777216;//每个窗口上限值

    private final HostAndPort defaultSocksProxyAddress;

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 9090 ;


    private final NettyClientConfig nettyClientConfig;

    public SmartClient(NettyClientConfig nettyClientConfig) {
        this.nettyClientConfig = nettyClientConfig;
        this.defaultSocksProxyAddress = nettyClientConfig.getDefaultSocksProxyAddress();
    }
    public <T extends SmartClientChannel> ListenableFuture<T> connectAsync(
            NiftyClientConnector<T> clientChannelConnector)
    {
        return connectAsync(clientChannelConnector,
                DEFAULT_CONNECT_TIMEOUT,
                DEFAULT_RECEIVE_TIMEOUT,
                DEFAULT_READ_TIMEOUT,
                DEFAULT_SEND_TIMEOUT,
                DEFAULT_MAX_FRAME_SIZE,
                defaultSocksProxyAddress);
    }
    public <T extends SmartClientChannel> ListenableFuture<T> connectAsync(
            NiftyClientConnector<T> clientChannelConnector,
            Duration connectTimeout,
            Duration receiveTimeout,
            Duration readTimeout,
            Duration sendTimeout,
            int maxFrameSize,
            HostAndPort socksProxyAddress)
    {
        checkNotNull(clientChannelConnector, "clientChannelConnector is null");
        //配置网络参数
        Bootstrap bootstrap = createClientBootstrap(socksProxyAddress);
        if(nettyClientConfig.getBootstrapOptions()!=null){
            for (SmartChannelOption smartChannelOption:nettyClientConfig.getBootstrapOptions()
                 ) {
                bootstrap.option(smartChannelOption.getOption(),smartChannelOption.getValue());
            }
        }
        //这里只处理链接超时
        if (connectTimeout != null) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) connectTimeout.roundTo(TimeUnit.MILLISECONDS));
        }
        //配置客户端流水线
        bootstrap.handler(clientChannelConnector.newChannelPipelineFactory(maxFrameSize, nettyClientConfig));
        //执行链接操作
        ChannelFuture nettyChannelFuture = clientChannelConnector.connect(bootstrap);
        //暂时不为以创建成功的channel后续处理
       /* nettyChannelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                if (channel != null && channel.isOpen()) {
                    allChannels.add(channel);
                }
            }
        });*/
        return new TSmartFuture(clientChannelConnector,
                receiveTimeout,
                readTimeout,
                sendTimeout,
                nettyChannelFuture);
    }



    private Bootstrap createClientBootstrap(HostAndPort socksProxyAddress)
    {
        if (socksProxyAddress != null) {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            ThriftClientDefBuilder builder =ThriftClientDef.newBuilder().name("nettyThriftClient")
                    .listen(socksProxyAddress.getPort())
                    .protocol(TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory()).getInputProtocolFactory())
                    .withProcessor(new Hello.Processor(new HelloImpl()));

            try {
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new SmartClientChannelInitializer(nettyClientConfig,builder.build()));

                // Start the client.
                ChannelFuture f = bootstrap.connect(HOST, PORT).sync();

                // Wait until the connection is closed.
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                // Shut down the event loop to terminate all threads.
                group.shutdownGracefully();
            }
            return bootstrap;
        }
        return null;
    }

    private static InetSocketAddress toInetAddress(HostAndPort hostAndPort)
    {
        return (hostAndPort == null) ? null : new InetSocketAddress(hostAndPort.getHostText(), hostAndPort.getPort());
    }

    private class TSmartFuture<T extends SmartClientChannel> extends AbstractFuture<T>
    {
        private TSmartFuture(final NiftyClientConnector<T> clientChannelConnector,
                             final Duration receiveTimeout,
                             final Duration readTimeout,
                             final Duration sendTimeout,
                             final ChannelFuture channelFuture)
        {
            channelFuture.addListener(new ChannelFutureListener()
            {
                @Override
                public void operationComplete(ChannelFuture future)
                        throws Exception
                {
                    try {
                        if (future.isSuccess()) {
                            Channel nettyChannel = future.channel();
                            T channel = clientChannelConnector.newThriftClientChannel(nettyChannel,
                                    nettyClientConfig);
                            channel.setReceiveTimeout(receiveTimeout);
                            channel.setReadTimeout(readTimeout);
                            channel.setSendTimeout(sendTimeout);
                            set(channel);
                        }
                        else if (future.isCancelled()) {
                            if (!cancel(true)) {
                                setException(new TTransportException("Unable to cancel client channel connection"));
                            }
                        }
                        else {
                            throw future.cause();
                        }
                    }
                    catch (Throwable t) {
                        setException(new TTransportException("Failed to connect client channel", t));
                    }
                }
            });
        }
    }
}
