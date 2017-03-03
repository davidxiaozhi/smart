package com.si.jupiter.smart.client.old;

import com.si.jupiter.smart.client.config.NettyClientConfig;
import com.si.jupiter.smart.client.config.ThriftClientDef;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.apache.thrift.protocol.TProtocolFactory;

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
