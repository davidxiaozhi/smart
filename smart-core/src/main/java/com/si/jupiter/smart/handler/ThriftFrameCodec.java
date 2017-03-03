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
package com.si.jupiter.smart.handler;

import com.si.jupiter.smart.codec.ThriftFrameDecoder;
import com.si.jupiter.smart.codec.ThriftFrameEncoder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.apache.thrift.protocol.TProtocolFactory;


public class ThriftFrameCodec extends ChannelDuplexHandler {
    private final ThriftFrameDecoder decoder;
    private final ThriftFrameEncoder encoder;

    public ThriftFrameCodec(int maxFrameSize, TProtocolFactory inputProtocolFactory)
    {
        this.decoder = new ThriftFrameDecoder(maxFrameSize, inputProtocolFactory);
        this.encoder = new ThriftFrameEncoder(maxFrameSize);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("decoder  for thrift");
        decoder.channelRead(ctx, msg);//decode里面的channelRead会依据情况触发下一个channelread的执行，因此这里不要再次出发
        //super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        encoder.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
