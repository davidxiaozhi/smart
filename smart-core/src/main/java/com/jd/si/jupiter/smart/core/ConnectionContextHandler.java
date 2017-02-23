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
package com.jd.si.jupiter.smart.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * 功能很简单仅仅是包装链接信息,链接建立好后包装链接状态信息供整个责任链使用
 */
public class ConnectionContextHandler extends ChannelInboundHandlerAdapter
{

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //相关链接状态信息包装,由ChannelHandlerContext持有,并向下传递
        SmartConnectionContext context = new SmartConnectionContext();
        context.setRemoteAddress(ctx.channel().remoteAddress());
        AttributeKey<SmartConnectionContext> remoteAddressKey= AttributeKey.newInstance("remoteAddress");
        Attribute<SmartConnectionContext> remoteAddressAttr= ctx.attr(remoteAddressKey);
        remoteAddressAttr.setIfAbsent(context);
        super.channelActive(ctx);//向下触发事件
    }
}
