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
package com.jd.si.jupiter.smart.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.TooLongFrameException;

import java.nio.channels.Channels;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;

public class ThriftFrameEncoder extends MessageToMessageEncoder<ThriftMessage>
{
    private final long maxFrameSize;

    private ByteBufAllocator byteBufAllocator;

    public ThriftFrameEncoder(long maxFrameSize)
    {

        this.maxFrameSize = maxFrameSize;
        byteBufAllocator = new UnpooledByteBufAllocator(false);
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, ThriftMessage message, List<Object> out) throws Exception {
        int frameSize = message.getBuffer().readableBytes();

        if (message.getBuffer().readableBytes() > maxFrameSize)
        {
            ctx.fireExceptionCaught(new TooLongFrameException(
                    String.format(
                            "Frame size exceeded on encode: frame was %d bytes, maximum allowed is %d bytes",
                            frameSize,
                            maxFrameSize)));
        }
        switch (message.getTransportType()) {
            case UNFRAMED:
                out.add(message.getBuffer());
            case FRAMED:
                ByteBuf frameSizeBuffer = byteBufAllocator.buffer(8);
                frameSizeBuffer.writeLong(message.getBuffer().readableBytes());
                out.add(wrappedBuffer(frameSizeBuffer, message.getBuffer()));

            case HEADER:
                throw new UnsupportedOperationException("Header transport is not supported");

            case HTTP:
                throw new UnsupportedOperationException("HTTP transport is not supported");

            default:
                throw new UnsupportedOperationException("Unrecognized transport type");
        }
    }
}
