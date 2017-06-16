package com.si.jupiter.smart.demo;

import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.clent.SmartClientFactory;
import com.si.jupiter.smart.core.AsyncPrepare;
import com.si.jupiter.smart.core.RequestContext;
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
public class ClientBatch {
    public static void main(String[] args) throws Exception {
        final ClientConfig<Test> conf = new ClientConfig();
        conf.setHost("127.0.0.1:5050;127.0.0.1:5051;127.0.0.1:5052").setRoute(RouteEnum.CONSISTENTHASH)
                .setConnectTimeout(2000)
                .setRequestTimeout(2000).setInterfaze(Test.class).setVersion("1.0.1").setWorkThreadSize(1).setSerializeType(SerializableEnum.PROTOBUF);
        final Test t = SmartClientFactory.makeClient(conf);



        for(int i=0;i<1000;i++) {

            /** 同步阻塞模式 **/
            Long stime = System.currentTimeMillis();
            RequestContext requestContext = RequestContext.getContext();
            String  para= "test syb"+(i%10);
            requestContext.setDispachKey(para);
            User u = t.test(para);
            System.out.println(u.toString() + " cost: " + (System.currentTimeMillis() - stime) + "ms");
        }
        for(int i=0;i<1000;i++) {
            /** 异步Future模式 **/
            final String  para= "test syb"+(i%10);
            Future<User> f = RpcContext.invokeWithFuture(new AsyncPrepare() {
                public void prepare() {
                    RequestContext requestContext = RequestContext.getContext();
                    requestContext.setDispachKey(para);
                    t.test(para);
                }
            });
            System.out.println(f.get());
        }
        for(int i=0;i<1000;i++) {
            /** 异步Callback模式 **/
            final String  para= "test syb"+(i%10);
            RpcContext.invokeWithCallback(new AsyncPrepare() {
                public void prepare() {
                    RequestContext requestContext = RequestContext.getContext();
                    requestContext.setDispachKey(para);
                    t.test(para);
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
}
