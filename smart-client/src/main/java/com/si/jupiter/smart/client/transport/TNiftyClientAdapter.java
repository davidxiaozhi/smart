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
import io.netty.channel.Channel;

public abstract class TNiftyClientAdapter implements TNiftyClientListener
{
    public void onFrameRead(Channel channel, ByteBuf buffer)
    {
        onInput(new TNiftyReadOnlyTransport(channel, buffer));
    }

    public void onChannelClosedOrDisconnected(Channel channel)
    {
    }

    public void onExceptionEvent(Exception e)
    {
    }

    /**
     * called when a frame is ready to be read.
     *
     * @param tNiftyReadOnlyTransport a one-time-use transport for the frame
     */
    public abstract void onInput(TNiftyReadOnlyTransport tNiftyReadOnlyTransport);

}
