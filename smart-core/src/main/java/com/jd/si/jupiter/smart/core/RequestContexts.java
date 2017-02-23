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

public class RequestContexts
{
    private static ThreadLocal<RequestContext> threadLocalContext = new ThreadLocal<RequestContext>();

    private RequestContexts()
    {
    }
    /**
     * 从本地线程存储程中获取当前线程处理的thrift请求
     */
    public static RequestContext getCurrentContext()
    {
        RequestContext currentContext = threadLocalContext.get();
        return currentContext;
    }


    /**
     * 将当前线程运行的请求放入thread-local,该操作仅仅被服务端调用,但是当ExecutorService分发给其他线程时也会调用,当另外一个线程
     * 也需要交互RequestContext
     * @param requestContext
     */
    public static void setCurrentContext(RequestContext requestContext)
    {
        threadLocalContext.set(requestContext);
    }

    /**
     * 移除当前线程的requestContext
     */
    public static void clearCurrentContext()
    {
        threadLocalContext.remove();
    }
}
