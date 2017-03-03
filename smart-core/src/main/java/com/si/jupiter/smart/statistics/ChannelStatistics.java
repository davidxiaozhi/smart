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
package com.si.jupiter.smart.statistics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Counters for number of channels open, generic traffic stats and maybe cleanup logic here.
 * 负责统计服务端总的链接数目,读写的网络流量
 */
public class ChannelStatistics  extends ChannelDuplexHandler implements SmartMetrics
{
    //channenl数目,这里其实就是链接数目
    private final AtomicInteger channelCount = new AtomicInteger(0);
    //接受数据量
    private final AtomicLong bytesRead = new AtomicLong(0);
    //输出数据量
    private final AtomicLong bytesWritten = new AtomicLong(0);
    //private final ChannelGroup allChannels;

    public static final String NAME = ChannelStatistics.class.getSimpleName();

    public ChannelStatistics()
    {
    }
    /*public ChannelStatistics(ChannelGroup allChannels)
    {
        this.allChannels = allChannels;
    }*/

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channelCount.incrementAndGet();
        //allChannels.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        channelCount.decrementAndGet();
        //allChannels.remove(ctx.channel());
    }
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        long size = calculateSize(msg);
        bytesRead.getAndAdd(size);
        super.channelRead(ctx, msg);
    }
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        long size = calculateSize(msg);
        bytesWritten.getAndAdd(size);
        super.write(ctx, msg, promise);
    }
    public int getChannelCount()
    {
        return channelCount.get();
    }

    public long getBytesRead()
    {
        return bytesRead.get();
    }

    public long getBytesWritten()
    {
        return bytesWritten.get();
    }
    /**
     * Calculate the size of the given {@link Object}.
     *
     * This implementation supports {@link ByteBuf} and {@link ByteBufHolder}. Sub-classes may override this.
     *
     * @param msg
     *            the msg for which the size should be calculated.
     * @return size the size of the msg or {@code -1} if unknown.
     */
    protected long calculateSize(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        if (msg instanceof ByteBufHolder) {
            return ((ByteBufHolder) msg).content().readableBytes();
        }
        return -1;
    }
}



