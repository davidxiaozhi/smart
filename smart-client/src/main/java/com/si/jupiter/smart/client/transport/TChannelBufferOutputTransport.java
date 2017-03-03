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
package com.si.jupiter.smart.client.transport;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * Implementation of {@link TTransport} that buffers the output of a single message,
 * so that an async client can grab the buffer and send it
 *
 * Allows for reusing the same transport to write multiple messages via
 */
public class TChannelBufferOutputTransport extends TTransport
{
    private static final int DEFAULT_MINIMUM_SIZE = 1024;

    // This threshold sets how many times the buffer must be under-utilized before we'll
    // reclaim some memory by reallocating it with half the current size
    private static final int UNDER_USE_THRESHOLD = 5;

    private ByteBuf outputBuffer;
    private final int minimumSize;
    private int bufferUnderUsedCounter;

    public TChannelBufferOutputTransport()
    {
        this.minimumSize = DEFAULT_MINIMUM_SIZE;
        outputBuffer = ByteBufAllocator.DEFAULT.buffer(this.minimumSize);
    }

    public TChannelBufferOutputTransport(int minimumSize)
    {
        this.minimumSize = Math.min(DEFAULT_MINIMUM_SIZE, minimumSize);
        outputBuffer = ByteBufAllocator.DEFAULT.buffer(this.minimumSize);
    }

    @Override
    public boolean isOpen()
    {
        return true;
    }

    @Override
    public void open() throws TTransportException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException
    {
        outputBuffer.writeBytes(buf, off, len);
    }

    /**
     * Resets the state of this transport so it can be used to write more messages
     */
    public void resetOutputBuffer()
    {
        int shrunkenSize = shrinkBufferSize();

        if (outputBuffer.writerIndex() < shrunkenSize) {
            // Less than the shrunken size of the buffer was actually used, so increment
            // the under-use counter
            ++bufferUnderUsedCounter;
        }
        else {
            // More than the shrunken size of the buffer was actually used, reset
            // the counter so we won't shrink the buffer soon
            bufferUnderUsedCounter = 0;
        }

        if (shouldShrinkBuffer()) {
            outputBuffer = ByteBufAllocator.DEFAULT.buffer(shrunkenSize);//修改新的内存没分配方式，代码review请注意
            bufferUnderUsedCounter = 0;
        } else {
            outputBuffer.clear();
        }
    }

    public ByteBuf getOutputBuffer()
    {
        return outputBuffer;
    }

    /**
     * Checks whether we should shrink the buffer, which should happen if we've under-used it
     * UNDER_USE_THRESHOLD times in a row
     */
    @SuppressWarnings("PMD.UselessParentheses")
    private boolean shouldShrinkBuffer()
    {
        // We want to shrink the buffer if it has been under-utilized UNDER_USE_THRESHOLD
        // times in a row, and the size after shrinking would not be smaller than the minimum size
        return bufferUnderUsedCounter > UNDER_USE_THRESHOLD &&
               shrinkBufferSize() >= minimumSize;
    }

    /**
     * Returns the size the buffer will be if we shrink it
     */
    private int shrinkBufferSize()
    {
        return outputBuffer.capacity() >> 1;
    }
}
