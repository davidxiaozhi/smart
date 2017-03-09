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

public interface SmartClientConnector<T extends RequestChannel>{
    /**
     * 通过bootstrap链接服务端，并得到ChannelFutuer,用于client发送消息
     * @param bootstrap
     * @return
     */
    ChannelFuture connect(Bootstrap bootstrap);

    /**
     * 主要用于配置客户端的channel
     * @param channel
     * @param clientConfig
     * @return
     */
    T newThriftClientChannel(Channel channel, NettyClientConfig clientConfig);

    /**
     * 用于创建客户端netty请求响应的pipeline
     * @param maxFrameSize 最大窗口大小
     * @param clientConfig　客户单netty相关配置项
     * @return
     */
    ChannelInitializer<? extends Channel> newChannelPipelineFactory(int maxFrameSize, NettyClientConfig clientConfig);
}
