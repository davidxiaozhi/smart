package com.si.jupiter.smart.demo;

import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.clent.SmartClientFactory;
import com.si.jupiter.smart.core.AsyncPrepare;
import com.si.jupiter.smart.core.RpcCallback;
import com.si.jupiter.smart.core.RpcContext;
import com.si.jupiter.smart.demo.idl.Test;
import com.si.jupiter.smart.demo.idl.User;
import com.si.jupiter.smart.network.SerializableEnum;
import com.si.jupiter.smart.route.RouteEnum;

import java.util.concurrent.Future;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 12:05
 */
public class Client {
    public static void main(String[] args) throws Exception {
        final ClientConfig<Test> conf = new ClientConfig();
        conf.setHost("127.0.0.1:5050;127.0.0.1:5051;127.0.0.1:5052").setRoute(RouteEnum.ROUND).setTimeout(2000).setInterfaze(Test.class).setVersion("1.0.1").setWorkThreadSize(1).setType(SerializableEnum.PROTOBUF);
        final Test t = SmartClientFactory.getServiceConsumer(conf);




        /** 同步阻塞模式 **/
        Long stime = System.currentTimeMillis();
        String u = t.test2();
        System.out.println(u.toString() + " cost: " + (System.currentTimeMillis() - stime) + "ms");

        /** 异步Future模式 **/
        Future<User> f = RpcContext.invokeWithFuture(new AsyncPrepare() {
            public void prepare() {
                t.test("test");
            }
        });
        System.out.println(f.get());

        /** 异步Callback模式 **/
        RpcContext.invokeWithCallback(new AsyncPrepare() {
            public void prepare() {
                t.test("test");
            }
        }, new RpcCallback() {
            public void success(Object value) {
                System.out.println("callback: " + value);
            }

            public void fail(Throwable error) {
                error.printStackTrace();
            }
        });
    }
}
