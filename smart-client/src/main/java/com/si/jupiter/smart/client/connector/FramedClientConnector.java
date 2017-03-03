/*
 * Copyright (C) 2012-2013 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.si.jupiter.smart.client.connector;

import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.google.common.net.HostAndPort;
import com.si.jupiter.smart.client.channel.FramedClientChannel;
import com.si.jupiter.smart.client.config.NettyClientConfig;
import com.si.jupiter.smart.handler.ThriftFrameCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/***
 * A {@link NiftyClientConnector} specialized for {@link FramedClientConnector}
 */
public class FramedClientConnector extends AbstractClientConnector<FramedClientChannel> {
    // TFramedTransport framing appears at the front of the message
    private static final int LENGTH_FIELD_OFFSET = 0;

    // TFramedTransport framing is four bytes long
    private static final int LENGTH_FIELD_LENGTH = 4;

    // TFramedTransport framing represents message size *not including* framing so no adjustment
    // is necessary
    private static final int LENGTH_ADJUSTMENT = 0;

    // The client expects to see only the message *without* any framing, this strips it off
    private static final int INITIAL_BYTES_TO_STRIP = LENGTH_FIELD_LENGTH;

    public FramedClientConnector(InetSocketAddress address)
    {
        this(address, defaultProtocolFactory());
    }

    public FramedClientConnector(HostAndPort address)
    {
        this(address, defaultProtocolFactory());
    }

    public FramedClientConnector(InetSocketAddress address, TDuplexProtocolFactory protocolFactory)
    {
        super(address, protocolFactory);
    }

    public FramedClientConnector(HostAndPort address, TDuplexProtocolFactory protocolFactory)
    {
        super(toSocketAddress(address), protocolFactory);
    }

    @Override
    public FramedClientChannel newThriftClientChannel(Channel nettyChannel, NettyClientConfig clientConfig)
    {
        FramedClientChannel channel = new FramedClientChannel(nettyChannel, clientConfig.getTimer(), getProtocolFactory());
        ChannelPipeline cp = nettyChannel.pipeline();
        ///TimeoutHandler.addToPipeline(cp);
        /*cp.addLast(new ReadTimeoutHandler(200, TimeUnit.MILLISECONDS));
        cp.addLast("thriftHandler", channel);
        cp.addLast(new WriteTimeoutHandler(200,TimeUnit.MILLISECONDS));*/
        return channel;
    }

    @Override
    public ChannelInitializer<? extends Channel> newChannelPipelineFactory(final int maxFrameSize, NettyClientConfig clientConfig)
    {
        return new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline cp = ch.pipeline();
                cp.addLast(new ReadTimeoutHandler(200,TimeUnit.MILLISECONDS));
                cp.addLast("ThriftFrameCodec",new ThriftFrameCodec(1024,getProtocolFactory().getInputProtocolFactory()));
                //这里暂时不进行ssl的处理
                cp.addLast(new WriteTimeoutHandler(200,TimeUnit.MILLISECONDS));
            }
        };
    }

}
