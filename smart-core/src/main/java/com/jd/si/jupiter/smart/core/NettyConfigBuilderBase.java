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

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelOption;
import io.netty.util.Timer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;

/*
 * Netty配置参数持有者
 */
public abstract class NettyConfigBuilderBase<T extends NettyConfigBuilderBase<T>>
{
    // These constants come directly from Netty but are private in Netty.
    //下面这些变量在netty当中都有,但是netty当中是私有
    public static final int DEFAULT_BOSS_THREAD_COUNT = 1;//默认BOSS线程数,用于处理链接请求建立
    //默认Worker线程数,用于处理请求
    public static final int DEFAULT_WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;

    //socket配置
    private final List<? extends SmartChannelOption> options = new LinkedList<>();
    private String smartName;//用来给线程命名
    private int bossThreadCount = DEFAULT_BOSS_THREAD_COUNT;//BOSS线程数,用于处理链接请求建立
    private int workerThreadCount = DEFAULT_WORKER_THREAD_COUNT;//Worker线程数,用于处理请求
    private ExecutorService bossThreadExecutor;//Boss线程池
    private ExecutorService workerThreadExecutor;//Worker线程池
    private Timer timer;//定时器用于 定时执行一次future

    /**
     * 返回引导类的选项配置视图(视图顾名思义只允许get)
     * @return
     */
    public List<? extends SmartChannelOption> getBootstrapOptions()
    {
        return Collections.unmodifiableList(options);
    }

    public T setTimer(Timer timer)
    {
        this.timer = timer;
        return (T) this;
    }

    protected Timer getTimer()
    {
        return timer;
    }

    /**
     * Sets an identifier which will be added to all thread created by the boss and worker
     * executors.
     *
     * @param niftyName
     * @return
     */
    public T setSmartName(String niftyName)
    {
        Preconditions.checkNotNull(niftyName, "smartName cannot be null");
        this.smartName = niftyName;
        return (T) this;
    }

    public String getSmartName()
    {
        return smartName;
    }

    public T setBossThreadExecutor(ExecutorService bossThreadExecutor)
    {
        this.bossThreadExecutor = bossThreadExecutor;
        return (T) this;
    }

    protected ExecutorService getBossExecutor()
    {
        return bossThreadExecutor;
    }

    /**
     * Sets the number of threads that will be used to manage
     * @param bossThreadCount
     * @return
     */
    public T setBossThreadCount(int bossThreadCount)
    {
        this.bossThreadCount = bossThreadCount;
        return (T) this;
    }

    protected int getBossThreadCount()
    {
        return bossThreadCount;
    }

    public T setWorkerThreadExecutor(ExecutorService workerThreadExecutor)
    {
        this.workerThreadExecutor = workerThreadExecutor;
        return (T) this;
    }

    protected ExecutorService getWorkerExecutor()
    {
        return workerThreadExecutor;
    }

    public T setWorkerThreadCount(int workerThreadCount)
    {
        this.workerThreadCount = workerThreadCount;
        return (T) this;
    }

    protected int getWorkerThreadCount()
    {
        return workerThreadCount;
    }

    // Magic alert ! Content of this class is considered ugly and magical.
    // For all intents and purposes this is to create a Map with the correct
    // key and value pairs for Netty's Bootstrap to consume.
    //
    // sadly Netty does not define any constant strings whatsoever for the proper key to
    // use and it's all based on standard java bean attributes.
    //
    // A ChannelConfig impl in netty is also tied with a socket, but since all
    // these configs are interfaces we can do a bit of magic hacking here.

    protected class Magic implements InvocationHandler
    {
        private final String prefix;

        public Magic(String prefix)
        {
            this.prefix = prefix;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable
        {
            // we are only interested in setters with single arg
            if (proxy != null) {
                if (method.getName().equals("toString")) {
                    return "this is a magic proxy";
                }
                else if (method.getName().equals("equals")) {
                    return Boolean.FALSE;
                }
                else if (method.getName().equals("hashCode")) {
                    return 0;
                }
            }
            // we don't support multi-arg setters
            if (method.getName().startsWith("set") && args.length == 1) {
                String attributeName = method.getName().substring(3);
                // camelCase it
                attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
                // now this is our key
                //options.add()
                //options.put(prefix + attributeName, args[0]);
                System.out.println(prefix + attributeName+"="+args[0]);
                return null;
            }
            throw new UnsupportedOperationException();
        }
    }
}
