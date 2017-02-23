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

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartConnectionContext implements ConnectionContext
{
    private SocketAddress remoteAddress;
    private Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public SocketAddress getRemoteAddress()
    {
        return remoteAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress)
    {
        this.remoteAddress = remoteAddress;
    }

    public Object getAttribute(String attributeName)
    {
        Preconditions.checkNotNull(attributeName);
        return attributes.get(attributeName);
    }

    public Object setAttribute(String attributeName, Object value)
    {
        Preconditions.checkNotNull(attributeName);
        Preconditions.checkNotNull(value);
        return attributes.put(attributeName, value);
    }

    public Object removeAttribute(String attributeName)
    {
        Preconditions.checkNotNull(attributeName);
        return attributes.remove(attributeName);
    }

    public Iterator<Map.Entry<String, Object>> attributeIterator()
    {
        return Collections.unmodifiableSet(attributes.entrySet()).iterator();
    }
}
