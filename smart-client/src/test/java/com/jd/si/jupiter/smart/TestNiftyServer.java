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
package com.jd.si.jupiter.smart;


import com.jd.si.jupiter.smart.core.*;
import com.jd.si.jupiter.smart.thrift.LogEntry;
import com.jd.si.jupiter.smart.thrift.ResultCode;
import com.jd.si.jupiter.smart.thrift.scribe;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.logging.Logger;

public class TestNiftyServer
{
    private static final Logger log = Logger.getLogger(TestNiftyServer.class.getSimpleName());
    private NettyServerTransport server;
    private int port;

    @BeforeEach
    public void setup()
    {
        server = null;
    }

    @AfterEach
    public void teardown()
            throws InterruptedException
    {
        if (server != null) {
            server.stop();
        }
    }

    private void startServer() throws InterruptedException {
        startServer(getThriftServerDefBuilder());
    }

    private void startServer(final ThriftServerDefBuilder thriftServerDefBuilder) throws InterruptedException {
        server = new NettyServerTransport(thriftServerDefBuilder.build(),
                                          NettyServerConfig.newBuilder().build(),
                                          null);
        server.start();
        port = server.getPort();
    }

    private ThriftServerDefBuilder getThriftServerDefBuilder()
    {
       /* return new ThriftServerDefBuilder()
                .listen(8090)
                .withProcessor(new scribe.Processor<>(new scribe.Iface() {
                    @Override
                    public ResultCode Log(List<LogEntry> messages)
                            throws TException
                    {
                        RequestContext context = RequestContexts.getCurrentContext();

                        for (LogEntry message : messages) {
                            log.info(String.format("[Client: %s] %s: %s",
                                    context.getConnectionContext().getRemoteAddress(),
                                    message.getCategory(),
                                    message.getMessage()));
                        }
                        return ResultCode.OK;
                    }
                }));*/
       return null;
    }

    private scribe.Client makeNiftyClient()
            throws TTransportException, InterruptedException
    {
        /*InetSocketAddress address = new InetSocketAddress("localhost", port);
        TTransport transport = new NiftyClient().connectSync(scribe.Client.class, new FramedClientConnector(address));
        TProtocol protocol = new TBinaryProtocol(transport);
        return new scribe.Client(protocol);*/
        return null;
    }

    @Test
    public void testBasic() throws InterruptedException, TException
    {
        startServer();
        scribe.Client client1 = makeNiftyClient();
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "aaa"))), ResultCode.OK);
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "bbb"))), ResultCode.OK);
        scribe.Client client2 = makeNiftyClient();
        Assertions.assertEquals(client2.Log(Arrays.asList(new LogEntry("client2", "ccc"))), ResultCode.OK);
    }

    @Test
    public void testMaxConnections() throws InterruptedException, TException
    {
        startServer(getThriftServerDefBuilder().limitConnectionsTo(1));
        scribe.Client client1 = makeNiftyClient();
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "aaa"))), ResultCode.OK);
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "bbb"))), ResultCode.OK);
        scribe.Client client2 = makeNiftyClient();
        try {
            client2.Log(Arrays.asList(new LogEntry("client2", "ccc")));
        } catch (TTransportException e) {
            // expected
        }
    }

    @Test
    public void testMaxConnections2() throws InterruptedException, TException
    {
        startServer(getThriftServerDefBuilder().limitConnectionsTo(1));
        scribe.Client client1 = makeNiftyClient();
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "aaa"))), ResultCode.OK);
        Assertions.assertEquals(client1.Log(Arrays.asList(new LogEntry("client1", "bbb"))), ResultCode.OK);
        scribe.Client client2 = makeNiftyClient();
        try {
            client2.Log(Arrays.asList(new LogEntry("client2", "ccc")));
        } catch (TTransportException e) {
            // expected
        }
        // now need to make sure we didn't double-decrement the number of connections, so try again
        scribe.Client client3 = makeNiftyClient();
        try {
            client3.Log(Arrays.asList(new LogEntry("client3", "ddd")));
            Assertions.fail("主动失败");
        } catch (TTransportException e) {
            // expected
        }
    }
}
