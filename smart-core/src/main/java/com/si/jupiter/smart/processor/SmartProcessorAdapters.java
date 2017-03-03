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
package com.si.jupiter.smart.processor;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.si.jupiter.smart.core.RequestContext;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.concurrent.ExecutionException;

public class SmartProcessorAdapters
{
    /**
     * 适配器,负责适配TProcessor并处理
     * @param standardThriftProcessor
     * @return
     */
    public static SmartProcessor processorFromTProcessor(final TProcessor standardThriftProcessor)
    {
        checkProcessMethodSignature();

        return new SmartProcessor()
        {
            public ListenableFuture<Boolean> process(TProtocol in, TProtocol out, RequestContext requestContext) throws TException
            {
                return Futures.immediateFuture(standardThriftProcessor.process(in, out));
            }
        };
    }

    /**
     * 创建一个工厂,用于总是返回相同的SmartProcessor处理独立的Thrift TProcessor
     * @param standardThriftProcessor
     * @return
     */
    public static SmartProcessorFactory factoryFromTProcessor(final TProcessor standardThriftProcessor)
    {
        checkProcessMethodSignature();

        return new SmartProcessorFactory()
        {
            public SmartProcessor getProcessor(TTransport transport)
            {
                return processorFromTProcessor(standardThriftProcessor);
            }
        };
    }

    /**
     * 创建一个SmartProcessorFactory 代理 Thrift TProcessorFactory 构造实例
     * @param standardThriftProcessorFactory
     * @return
     */
    public static SmartProcessorFactory factoryFromTProcessorFactory(final TProcessorFactory standardThriftProcessorFactory)
    {
        checkProcessMethodSignature();

        return new SmartProcessorFactory()
        {
            public SmartProcessor getProcessor(TTransport transport)
            {
                return processorFromTProcessor(standardThriftProcessorFactory.getProcessor
                        (transport));
            }
        };
    }

    /**
     * 通过内部构造一个thrift的TProcessor实现来适配 SmartProcessor 处理 thrift 协议 ,不过SmartRequestContext永远会传递null
     * @param niftyProcessor
     * @return
     */
    public static TProcessor processorToTProcessor(final SmartProcessor niftyProcessor)
    {
        return new TProcessor()
        {
            public boolean process(TProtocol in, TProtocol out) throws TException
            {
                try {
                    return niftyProcessor.process(in, out, null).get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new TException(e);
                } catch (ExecutionException e) {
                    throw new TException(e);
                }
            }
        };
    }

    /**
     * 通过内部构造一个thrift的TProcessorFactory,这样可以总返回一个TProcessor实现来适配 SmartProcessor 处理 thrift 协议 ,不过SmartRequestContext永远会传递null
     * @param niftyProcessor
     * @return
     */
    public static TProcessorFactory processorToTProcessorFactory(final SmartProcessor niftyProcessor)
    {
        return new TProcessorFactory(processorToTProcessor(niftyProcessor));
    }

    /**
     * thrift TProcessorFactory 到  SmartProcessorFactory 的适配
     * @param niftyProcessorFactory
     * @return
     */
    public static TProcessorFactory processorFactoryToTProcessorFactory(final SmartProcessorFactory niftyProcessorFactory)
    {
        return new TProcessorFactory(null) {
            @Override
            public TProcessor getProcessor(TTransport trans)
            {
                return processorToTProcessor(niftyProcessorFactory.getProcessor(trans));
            }
        };
    }

    /**
     * 检查验证TProcessor实现的方法签名是否拥有process方法
     */
    private static void checkProcessMethodSignature()
    {
        try {
            TProcessor.class.getMethod("process", TProtocol.class, TProtocol.class);
        }
        catch (NoSuchMethodException e) {
            // Facebook's TProcessor variant needs processor adapters from a different package
            throw new IllegalStateException("The loaded TProcessor class is not supported by version of the adapters");
        }
    }
}
