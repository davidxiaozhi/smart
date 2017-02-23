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
package com.jd.si.jupiter.smart.client.config;

import com.google.common.net.HostAndPort;
import com.jd.si.jupiter.smart.core.SmartChannelOption;
import io.netty.channel.ChannelOption;
import io.netty.util.Timer;

import java.util.List;
import java.util.Map;

public class NettyClientConfig
{
    //private final ThriftClientDef clientDef;
    private final List<? extends SmartChannelOption> bootstrapOptions;
    private final HostAndPort defaultSocksProxyAddress;
    private final Timer timer;
    private final int threadCount;
    private final String smartName;//用来给线程命名
    //private final ChannelInitializer channelInitializer;

    public NettyClientConfig(
                             ///ThriftClientDef clientDef,
                             //ChannelInitializer channelInitializer,
                             List<? extends SmartChannelOption> bootstrapOptions,
                             HostAndPort defaultSocksProxyAddress,
                             Timer timer,
                             int threadCount, String smartName)

    {
/*        this.clientDef = clientDef;
        this.channelInitializer = channelInitializer;*/
        this.bootstrapOptions = bootstrapOptions;
        this.defaultSocksProxyAddress = defaultSocksProxyAddress;
        this.timer = timer;
        this.threadCount = threadCount;
        this.smartName = smartName;
    }

    public List<? extends SmartChannelOption> getBootstrapOptions()
    {
        return bootstrapOptions;
    }


    public HostAndPort getDefaultSocksProxyAddress()
    {
        return defaultSocksProxyAddress;
    }

    public Timer getTimer()
    {
        return timer;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public String getSmartName() {
        return smartName;
    }

    /*public ThriftClientDef getClientDef() {
        return clientDef;
    }

    public ChannelInitializer getChannelInitializer() {
        return channelInitializer;
    }*/

    public static NettyClientConfigBuilder newBuilder()
    {
        return new NettyClientConfigBuilder();
    }

}
