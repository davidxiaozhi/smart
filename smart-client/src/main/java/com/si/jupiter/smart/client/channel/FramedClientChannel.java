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
package com.si.jupiter.smart.client.channel;

import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.Timer;

public class FramedClientChannel extends AbstractClientChannel {
    public FramedClientChannel(Channel channel, Timer timer, TDuplexProtocolFactory protocolFactory) {
        super(channel, timer, protocolFactory);
    }

    @Override
    protected ByteBuf extractResponse(Object message) {
        if (!(message instanceof ByteBuf)) {
            return null;
        }

        ByteBuf buffer = (ByteBuf) message;
        buffer = (ByteBuf) message;
        if (!buffer.isReadable()) {
            return null;
        }

        return buffer;
    }

    @Override
    protected ChannelFuture writeRequest(ByteBuf request) {
        return getNettyChannel().write(request);
    }

}
