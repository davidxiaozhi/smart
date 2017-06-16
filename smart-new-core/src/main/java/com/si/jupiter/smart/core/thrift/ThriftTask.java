package com.si.jupiter.smart.core.thrift;

import com.si.jupiter.smart.core.FuturesManager;
import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.RpcResult;
import com.si.jupiter.smart.demo.thrift.Hello;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class ThriftTask implements Runnable{
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftTask.class);
        private final NetworkProtocol msg;

        public ThriftTask(NetworkProtocol msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {

                Hello.Client client =new  Hello.Client.Factory().getClient(ThriftUtils.getProtocolByType(ProtocolType.TCompactProtocol,new ByteArrayInputStream(msg.getContent())));
                RpcResult result = new RpcResult();
                result.setValue(client.recv_helloString());
                FuturesManager.release(msg.getSequence(), result);
            } catch (Exception e) {
                LOGGER.error("Client handler fail.", e);
            }
        }
    }