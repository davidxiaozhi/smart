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

import com.si.jupiter.smart.client.channel.RequestChannel;
import com.si.jupiter.smart.client.config.NettyClientConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;

public interface NiftyClientConnector<T extends RequestChannel>{

    ChannelFuture connect(Bootstrap bootstrap);

    T newThriftClientChannel(Channel channel, NettyClientConfig clientConfig);

    ChannelInitializer<? extends Channel> newChannelPipelineFactory(int maxFrameSize, NettyClientConfig clientConfig);
}
