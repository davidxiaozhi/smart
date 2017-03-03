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


import com.si.jupiter.smart.utils.Duration;
import io.netty.channel.Channel;

public interface SmartClientChannel extends RequestChannel {
    /**
     * Sets a timeout used to limit elapsed time for sending a message.
     *
     * @param sendTimeout
     */
    void setSendTimeout(Duration sendTimeout);

    /**
     * Returns the timeout most recently set by
     * {@link SmartClientChannel#setSendTimeout(Duration)}
     *
     * @return
     */
    Duration getSendTimeout();

    /**
     * Sets a timeout used to limit elapsed time between successful send, and reception of the
     * response.
     *
     * @param receiveTimeout
     */
    void setReceiveTimeout( Duration receiveTimeout);

    /**
     * Returns the timeout most recently set by
     * {@link SmartClientChannel#setReceiveTimeout(Duration)}
     *
     * @return
     */
    Duration getReceiveTimeout();

    /**
     * Sets a timeout used to limit the time that the client waits for data to be sent by the server.
     *
     * @param readTimeout
     */
    void setReadTimeout( Duration readTimeout);

    /**
     * Returns the timeout most recently set by
     * {@link SmartClientChannel#setReadTimeout(Duration)}
     *
     * @return
     */
    Duration getReadTimeout();

    /**
     * Executes the given {@link Runnable} on the I/O thread that manages reads/writes for this
     * channel.
     *
     * @param runnable
     */
    void executeInIoThread(Runnable runnable);

    Channel getNettyChannel();
}
