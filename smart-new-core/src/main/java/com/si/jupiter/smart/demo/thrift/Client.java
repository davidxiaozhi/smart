package com.si.jupiter.smart.demo.thrift;

import com.si.jupiter.smart.clent.SmartClientFactory;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.core.AsyncPrepare;
import com.si.jupiter.smart.core.RpcContext;
import com.si.jupiter.smart.core.thrift.ProtocolType;
import com.si.jupiter.smart.core.thrift.ThriftConfig;
import com.si.jupiter.smart.network.SerializableEnum;
import com.si.jupiter.smart.route.RouteEnum;
import org.apache.thrift.TException;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 12:05
 */
public class Client {
    public static void main(String[] args) throws Exception {
        final ClientConfig<Hello.Iface> conf = new ClientConfig();
        conf.setHost("127.0.0.1:5050")
                .setRoute(RouteEnum.ROUND)
                .setRequestTimeout(10000).setInterfaze(Hello.Iface.class)
                .setVersion("1.0.1")
                .setWorkThreadSize(1)
                .setSerializeType(SerializableEnum.THRIFT)
                .setThriftConfig(new ThriftConfig(Hello.Iface.class,Hello.Client.class, ProtocolType.TCompactProtocol));
        final Hello.Iface t = SmartClientFactory.makeClient(conf);




        /** 同步阻塞模式 **/
       /* Long stime = System.currentTimeMillis();
        String u = t.helloString("test");
        System.out.println(u.toString() + " cost: " + (System.currentTimeMillis() - stime) + "ms");*/

        /** 异步Future模式 **/
        for(int i=0; i<=10;i++) {
            final String para = "test"+i;
            Future<String> f = RpcContext.invokeWithFuture(new AsyncPrepare() {
                public void prepare() {
                    try {
                        t.helloString(para);
                    } catch (TException e) {
                        e.printStackTrace();
                    }
                }
            });
            try{
                System.out.println("the thrift result:"+f.get(200, TimeUnit.MILLISECONDS));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        /** 异步Callback模式 **/
       /* RpcContext.invokeWithCallback(new AsyncPrepare() {
            public void prepare() {
                try {
                    t.helloString("test");
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        }, new RpcCallback() {
            public void success(Object value) {
                System.out.println("callback: " + value);
            }

            public void fail(Throwable error) {
                error.printStackTrace();
            }
        });*/
    }
}
