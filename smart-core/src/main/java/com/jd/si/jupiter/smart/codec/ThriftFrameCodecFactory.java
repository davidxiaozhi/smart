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

import io.netty.channel.ChannelHandler;
import org.apache.thrift.protocol.TProtocolFactory;

/**
 * 构建thrift frame协议编解码器工厂
 */
public interface ThriftFrameCodecFactory
{
    ChannelHandler create(int maxFrameSize, TProtocolFactory defaultProtocolFactory);
}
