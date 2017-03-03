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

import com.google.common.net.HttpHeaders;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.*;
import io.netty.util.Timer;
import org.apache.thrift.transport.TTransportException;

import java.util.Map;

public class HttpClientChannel extends AbstractClientChannel {
    private final Channel underlyingNettyChannel;
    private final String hostName;
    private Map<String, String> headerDictionary;
    private final String endpointUri;

    public HttpClientChannel(Channel channel,
                                Timer timer,
                                TDuplexProtocolFactory protocolFactory,
                                String hostName,
                                String endpointUri) {
        super(channel, timer, protocolFactory);

        this.underlyingNettyChannel = channel;
        this.hostName = hostName;
        this.endpointUri = endpointUri;
    }

    @Override
    public Channel getNettyChannel() {
        return underlyingNettyChannel;
    }

    @Override
    protected ByteBuf extractResponse(Object message) throws TTransportException
    {
        if (!(message instanceof DefaultFullHttpResponse)) {
            return null;
        }

        DefaultFullHttpResponse httpResponse = (DefaultFullHttpResponse) message;

        if (!httpResponse.getStatus().equals(HttpResponseStatus.OK)) {
            throw new TTransportException("HTTP response had non-OK status: " + httpResponse
                    .getStatus().toString());
        }

        ByteBuf content = httpResponse.content();

        if (!content.isReadable()) {
            return null;
        }

        return content;
    }

    @Override
    protected ChannelFuture writeRequest(ByteBuf request)
    {
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST,
                                                         endpointUri,request);

        httpRequest.headers().add(HttpHeaders.HOST, hostName);
        httpRequest.headers().add(HttpHeaders.CONTENT_LENGTH, request.readableBytes());
        httpRequest.headers().add(HttpHeaders.CONTENT_TYPE, "application/x-thrift");
        httpRequest.headers().add(HttpHeaders.ACCEPT, "application/x-thrift");
        httpRequest.headers().add(HttpHeaders.USER_AGENT, "Java/Swift-HttpThriftClientChannel");

        if (headerDictionary != null) {
            for (Map.Entry<String, String> entry : headerDictionary.entrySet()) {
                httpRequest.headers().add(entry.getKey(), entry.getValue());
            }
        }
        return underlyingNettyChannel.write(httpRequest);
    }

    public void setHeaders(Map<String, String> headers)
    {
        this.headerDictionary = headers;
    }

}
