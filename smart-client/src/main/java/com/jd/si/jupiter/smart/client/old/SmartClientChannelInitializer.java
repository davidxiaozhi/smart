package com.jd.si.jupiter.smart.client.old;

import com.jd.si.jupiter.smart.client.config.NettyClientConfig;
import com.jd.si.jupiter.smart.client.config.ThriftClientDef;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.thrift.protocol.TProtocolFactory;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  16-11-9 上午11:01
 * Last Update:  16-11-9 上午11:01
 */
public class SmartClientChannelInitializer extends ChannelInitializer {
    private final ThriftClientDef def;
    private final NettyClientConfig clientConfig;

    public SmartClientChannelInitializer(NettyClientConfig clientConfig,ThriftClientDef def) {
        this.clientConfig = clientConfig;
        this.def = def;
    }

    @Override
    protected void initChannel(Channel clientChannel) throws Exception {
        ChannelPipeline cp = clientChannel.pipeline();
        //主要解决无边框消息传输
        TProtocolFactory tProtocolFactory = def.getDuplexProtocolFactory().getInputProtocolFactory();
        cp.addLast("frameCodec",def.getThriftFrameCodecFactory().create(def.getMaxFrameSize(),tProtocolFactory));
    }
}
