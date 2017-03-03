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
package com.si.jupiter.smart.client.config;

import com.si.jupiter.smart.codec.DefaultThriftFrameCodecFactory;
import com.si.jupiter.smart.codec.ThriftFrameCodecFactory;
import com.si.jupiter.smart.duplex.TDuplexProtocolFactory;
import com.si.jupiter.smart.processor.SmartProcessor;
import com.si.jupiter.smart.processor.SmartProcessorAdapters;
import com.si.jupiter.smart.processor.SmartProcessorFactory;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkState;

/**
 * Builder for the Thrift Server descriptor. Example :
 * <code>
 * new ThriftClientDefBuilder()
 * .listen(config.getServerPort())
 * .limitFrameSizeTo(config.getMaxFrameSize())
 * .withProcessor(new FacebookService.Processor(new MyFacebookBase()))
 * .using(Executors.newFixedThreadPool(5))
 * .build();
 * </code>
 * You can then pass ThriftClientDef to guice via a multibinder.
 */
public abstract class ThriftClientDefBuilderBase<T extends ThriftClientDefBuilderBase<T>>
{
    private static final AtomicInteger ID = new AtomicInteger(1);
    private ThriftFrameCodecFactory thriftFrameCodecFactory;
    private int serverPort;
    private int maxFrameSize;
    private int maxConnections;
    private int queuedResponseLimit;
    private SmartProcessorFactory niftyProcessorFactory;
    private TProcessorFactory thriftProcessorFactory;
    private TDuplexProtocolFactory duplexProtocolFactory;
    private Executor executor;
    private String name = "com.jd.si.jupiter.smart-" + ID.getAndIncrement();
    private long clientIdleTimeout;
    private long taskTimeout;
    private long queueTimeout;
    //private SmartSecurityFactory securityFactory;
    //private TransportAttachObserver transportAttachObserver;

    /**
     * The default maximum allowable size for a single incoming thrift request or outgoing thrift
     * response. A server can configure the actual maximum to be much higher (up to 0x7FFFFFFF or
     * almost 2 GB). This default could also be safely bumped up, but 64MB is chosen simply
     * because it seems reasonable that if you are sending requests or responses larger than
     * that, it should be a conscious decision (something you must manually configure).
     */
    private static final int MAX_FRAME_SIZE = 64 * 1024 * 1024;

    /**
     * 依据默认值创建ThriftServer 但是请注意,后续这些默认值并不是都很合理
     */
    public ThriftClientDefBuilderBase()
    {
        this.serverPort = 8080;
        this.maxFrameSize = MAX_FRAME_SIZE;
        this.maxConnections = 0;
        this.queuedResponseLimit = 16;
        this.duplexProtocolFactory = TDuplexProtocolFactory.fromSingleFactory(new TBinaryProtocol.Factory(true, true));
        this.executor = new Executor()
        {
            @Override
            public void execute(Runnable runnable)
            {
                runnable.run();
            }
        };
        this.clientIdleTimeout = 0;
        this.taskTimeout = 0;
        this.queueTimeout = 0;
        this.thriftFrameCodecFactory = new DefaultThriftFrameCodecFactory();
    }

    /**
     * Give the endpoint a more meaningful name.
     */
    public T name(String name)
    {
        this.name = name;
        return (T) this;
    }

    /**
     * Listen to this port.
     */
    public T listen(int serverPort)
    {
        this.serverPort = serverPort;
        return (T) this;
    }

    /**
     * Specify protocolFactory for both input and output
     */
    public T protocol(TDuplexProtocolFactory tProtocolFactory)
    {
        this.duplexProtocolFactory = tProtocolFactory;
        return (T) this;
    }

    public T protocol(TProtocolFactory tProtocolFactory)
    {
        this.duplexProtocolFactory = TDuplexProtocolFactory.fromSingleFactory(tProtocolFactory);
        return (T) this;
    }

    /**
     * Specify the TProcessor.
     */
    public T withProcessor(final SmartProcessor processor)
    {
        this.niftyProcessorFactory = new SmartProcessorFactory() {
            @Override
            public SmartProcessor getProcessor(TTransport transport)
            {
                return processor;
            }
        };
        return (T) this;
    }

    public T withProcessor(TProcessor processor)
    {
        this.thriftProcessorFactory = new TProcessorFactory(processor);
        return (T) this;
    }

    /**
     * Anohter way to specify the TProcessor.
     */
    public T withProcessorFactory(SmartProcessorFactory processorFactory)
    {
        this.niftyProcessorFactory = processorFactory;
        return (T) this;
    }

    /**
     * Anohter way to specify the TProcessor.
     */
    public T withProcessorFactory(TProcessorFactory processorFactory)
    {
        this.thriftProcessorFactory = processorFactory;
        return (T) this;
    }

    /**
     * Set frame size limit.  Default is MAX_FRAME_SIZE
     */
    public T limitFrameSizeTo(int maxFrameSize)
    {
        this.maxFrameSize = maxFrameSize;
        return (T) this;
    }

    /**
     * Set maximum number of connections. Default is 0 (unlimited)
     */
    public T limitConnectionsTo(int maxConnections)
    {
        this.maxConnections = maxConnections;
        return (T) this;
    }

    /**
     * Limit number of queued responses per connection, before pausing reads
     * to catch up.
     */
    public T limitQueuedResponsesPerConnection(int queuedResponseLimit)
    {
        this.queuedResponseLimit = queuedResponseLimit;
        return (T) this;
    }

    /**
     * Specify timeout during which if connected client doesn't send a message, server
     * will disconnect the client
     */
    public T clientIdleTimeout(long clientIdleTimeout)
    {
        this.clientIdleTimeout = clientIdleTimeout;
        return (T) this;
    }

    /**
     * Specify timeout during which:
     * 1. if a task remains on the executor queue, server will cancel the task when it is dispatched.
     * 2. if a task is scheduled but does not finish processing, server will send timeout exception back.
     */
    public T taskTimeout(long taskTimeout)
    {
        this.taskTimeout = taskTimeout;
        return (T) this;
    }

    /**
     * Specify timeout during which if a task remains on the executor queue, server will cancel the
     *    task when it is dispatched.  The timeout is the minimum of taskTimeout and queueTimeout
     */
    public T queueTimeout(long queueTimeout)
    {
        this.queueTimeout = queueTimeout;
        return (T) this;
    }

    public T thriftFrameCodecFactory(ThriftFrameCodecFactory thriftFrameCodecFactory)
    {
        this.thriftFrameCodecFactory = thriftFrameCodecFactory;
        return (T) this;
    }

    /**
     * Specify an executor for thrift processor invocations ( i.e. = THaHsServer )
     * By default invocation happens in Netty single thread
     * ( i.e. = TNonBlockingServer )
     */
    public T using(Executor exe)
    {
        this.executor = exe;
        return (T) this;
    }

    /**
     * Build the ThriftClientDef
     */
    public ThriftClientDef build()
    {
        checkState(niftyProcessorFactory != null || thriftProcessorFactory != null,
                   "Processor not defined!");
        checkState(niftyProcessorFactory == null || thriftProcessorFactory == null,
                   "TProcessors will be automatically adapted to SmartProcessors, don't specify both");
        checkState(maxConnections >= 0, "maxConnections should be 0 (for unlimited) or positive");

        if (niftyProcessorFactory == null)
        {
            niftyProcessorFactory = SmartProcessorAdapters.factoryFromTProcessorFactory(thriftProcessorFactory);
        }

        return new ThriftClientDef(
                name,
                serverPort,
                maxFrameSize,
                queuedResponseLimit,
                maxConnections,
                niftyProcessorFactory,
                duplexProtocolFactory,
                clientIdleTimeout,
                taskTimeout,
                queueTimeout,
                thriftFrameCodecFactory,
                executor);
    }
}
