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
package com.jd.si.jupiter.smart.processor;

import com.google.common.util.concurrent.ListenableFuture;
import com.jd.si.jupiter.smart.core.RequestContext;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;

public interface SmartProcessor
{
    /**
     * thrift Processor 抽象,由适配器负责二者的转换
     * @param in
     * @param out
     * @param requestContext 这里请求上下文只是临时封装,留作扩展
     * @return
     * @throws TException
     */
     ListenableFuture<Boolean> process(TProtocol in, TProtocol out, RequestContext requestContext) throws TException;
}
