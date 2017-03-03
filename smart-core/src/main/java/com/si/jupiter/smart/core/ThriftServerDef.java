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
package com.si.jupiter.smart.core;

import com.si.jupiter.smart.codec.ThriftFrameCodecFactory;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.si.jupiter.smart.processor.SmartProcessorFactory;

import java.util.concurrent.Executor;

/**
 * Descriptor for a Thrift Server. This defines a listener port that Smart need to start a Thrift endpoint.
 */
public class ThriftServerDef
{
    private final int serverPort;
    private final int maxFrameSize;
    private final int maxConnections;
    private final int queuedResponseLimit;
    private final SmartProcessorFactory processorFactory;
    private final TDuplexProtocolFactory duplexProtocolFactory;

    private final long clientIdleTimeout;
    private final long taskTimeout;
    private final long queueTimeout;

    private final ThriftFrameCodecFactory thriftFrameCodecFactory;
    private final Executor executor;
    private final String name;

    public ThriftServerDef(
            String name,
            int serverPort,
            int maxFrameSize,
            int queuedResponseLimit,
            int maxConnections,
            SmartProcessorFactory processorFactory,
            TDuplexProtocolFactory duplexProtocolFactory,
            long clientIdleTimeout,
            long taskTimeout,
            long queueTimeout,
            ThriftFrameCodecFactory thriftFrameCodecFactory,
            Executor executor)
    {
        this.name = name;
        this.serverPort = serverPort;
        this.maxFrameSize = maxFrameSize;
        this.maxConnections = maxConnections==0?100:maxConnections;
        this.queuedResponseLimit = queuedResponseLimit;
        this.processorFactory = processorFactory;
        this.duplexProtocolFactory = duplexProtocolFactory;
        this.clientIdleTimeout = clientIdleTimeout;
        this.taskTimeout = taskTimeout;
        this.queueTimeout = queueTimeout;
        this.thriftFrameCodecFactory = thriftFrameCodecFactory;
        this.executor = executor;
    }

    public static ThriftServerDefBuilder newBuilder()
    {
        return new ThriftServerDefBuilder();
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public int getMaxFrameSize()
    {
        return maxFrameSize;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public int getQueuedResponseLimit()
    {
        return queuedResponseLimit;
    }

    public SmartProcessorFactory getProcessorFactory()
    {
        return processorFactory;
    }

    public TDuplexProtocolFactory getDuplexProtocolFactory()
    {
        return duplexProtocolFactory;
    }

    public long getClientIdleTimeout() {
        return clientIdleTimeout;
    }

    public long getTaskTimeout() { return taskTimeout; }

    public long getQueueTimeout() { return queueTimeout; }

    public Executor getExecutor()
    {
        return executor;
    }

    public String getName()
    {
        return name;
    }

    public ThriftFrameCodecFactory getThriftFrameCodecFactory()
    {
        return thriftFrameCodecFactory;
    }

}
