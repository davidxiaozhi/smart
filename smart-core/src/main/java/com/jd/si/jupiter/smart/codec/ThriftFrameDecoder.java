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

import com.jd.si.jupiter.smart.core.TSmartTransport;
import com.jd.si.jupiter.smart.core.ThriftTransportType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TTransportException;

import java.nio.channels.Channels;

/**
 * 基于netty4 LengthFieldBasedFrameDecoder 重新实现Thrift解码器
 */
public class ThriftFrameDecoder extends LengthFieldBasedFrameDecoder
{
    public static final int MESSAGE_FRAME_SIZE = 8;
    private final int maxFrameSize;
    private final TProtocolFactory inputProtocolFactory;

    public ThriftFrameDecoder(int maxFrameSize, TProtocolFactory inputProtocolFactory)
    {
        super(maxFrameSize, 0, MESSAGE_FRAME_SIZE);
        this.maxFrameSize = maxFrameSize;
        this.inputProtocolFactory = inputProtocolFactory;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
        if (!buffer.isReadable()) {
            return null;
        }
        short firstByte = buffer.getUnsignedByte(0);
        if (firstByte >= 0x80) {
            ByteBuf messageBuffer = tryDecodeUnframedMessage(ctx, buffer, inputProtocolFactory);

            if (messageBuffer == null) {
                return null;
            }

            // A non-zero MSB for the first byte of the message implies the message starts with a
            // protocol id (and thus it is unframed).
            return new ThriftMessage(messageBuffer,ThriftTransportType.UNFRAMED);
        } else if (buffer.readableBytes() < MESSAGE_FRAME_SIZE) {
            // Expecting a framed message, but not enough bytes available to read the frame size
            return null;
        } else {
            ByteBuf messageBuffer = tryDecodeFramedMessage(ctx, buffer, true);

            if (messageBuffer == null) {
                return null;
            }

            // Messages with a zero MSB in the first byte are framed messages
            return new ThriftMessage(messageBuffer, ThriftTransportType.FRAMED);
        }
    }

    protected ByteBuf tryDecodeFramedMessage(ChannelHandlerContext ctx,
                                                   ByteBuf buffer,
                                                   boolean stripFraming)
    {
        // Framed messages are prefixed by the size of the frame (which doesn't include the
        // framing itself).

        int messageStartReaderIndex = buffer.readerIndex();
        int messageContentsOffset;

        if (stripFraming) {
            messageContentsOffset = messageStartReaderIndex + MESSAGE_FRAME_SIZE;
        }
        else {
            messageContentsOffset = messageStartReaderIndex;
        }

        // The full message is larger by the size of the frame size prefix
        int messageLength = buffer.getInt(messageStartReaderIndex) + MESSAGE_FRAME_SIZE;
        int messageContentsLength = messageStartReaderIndex + messageLength - messageContentsOffset;

        if (messageContentsLength > maxFrameSize) {
            ctx.fireExceptionCaught(new TooLongFrameException("Maximum frame size of " + maxFrameSize +
                    " exceeded"));
        }

        if (messageLength == 0) {
            // Zero-sized frame: just ignore it and return nothing
            buffer.readerIndex(messageContentsOffset);
            return null;
        } else if (buffer.readableBytes() < messageLength) {
            // Full message isn't available yet, return nothing for now
            return null;
        } else {
            // Full message is available, return it
            ByteBuf messageBuffer = extractFrame(buffer,
                                                       messageContentsOffset,
                                                       messageContentsLength);
            buffer.readerIndex(messageStartReaderIndex + messageLength);
            return messageBuffer;
        }
    }

    /**
     * 无边框只能通过协议解析thrift流的大小
     * @param ctx
     * @param buffer
     * @param inputProtocolFactory
     * @return
     * @throws TException
     */
    protected ByteBuf tryDecodeUnframedMessage(ChannelHandlerContext ctx,
                                                     ByteBuf buffer,
                                                     TProtocolFactory inputProtocolFactory)
            throws TException
    {
        // Perform a trial decode, skipping through
        // the fields, to see whether we have an entire message available.

        int messageLength = 0;
        int messageStartReaderIndex = buffer.readerIndex();

        try {
            TSmartTransport decodeAttemptTransport =
                    new TSmartTransport(ctx.channel(), buffer, ThriftTransportType.UNFRAMED);
            int initialReadBytes = decodeAttemptTransport.getReadByteCount();
            TProtocol inputProtocol =
                    inputProtocolFactory.getProtocol(decodeAttemptTransport);
            // Skip through the message
            inputProtocol.readMessageBegin();
            TProtocolUtil.skip(inputProtocol, TType.STRUCT);
            inputProtocol.readMessageEnd();

            messageLength = decodeAttemptTransport.getReadByteCount() - initialReadBytes;
        } catch (TTransportException te) {
            // No complete message was decoded: ran out of bytes
            return null;
        }
        catch (IndexOutOfBoundsException e){
            return null;
        }
        finally {
            if (buffer.readerIndex() - messageStartReaderIndex > maxFrameSize) {
                ctx.fireExceptionCaught( new TooLongFrameException("Maximum frame size of " + maxFrameSize + " exceeded"));
            }

            buffer.readerIndex(messageStartReaderIndex);
        }

        if (messageLength <= 0) {
            return null;
        }

        // We have a full message in the read buffer, slice it off
        ByteBuf messageBuffer =
                extractFrame(buffer, messageStartReaderIndex, messageLength);
        buffer.readerIndex(messageStartReaderIndex + messageLength);
        return messageBuffer;
    }

    protected ByteBuf extractFrame(ByteBuf buffer, int index, int length)
    {
        // Slice should be sufficient here (and avoids the copy in LengthFieldBasedFrameDecoder)
        // because we know no one is going to modify the contents in the read buffers.
        return buffer.slice(index, length);
    }
}
