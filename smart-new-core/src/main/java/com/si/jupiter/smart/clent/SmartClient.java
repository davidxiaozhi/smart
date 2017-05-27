package com.si.jupiter.smart.clent;


import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.core.ProtocolProcesserImpl;
import com.si.jupiter.smart.core.RequestContext;
import com.si.jupiter.smart.network.proxy.RpcClientProxy;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:01
 */
public class SmartClient<T> {
    private volatile T client;
    private ClientConfig conf;
    private ProtocolProcesserImpl processer;
    private volatile ClientManager manager;

    public SmartClient(ClientConfig conf) {
        this.conf = conf;
    }

    /**
     * 1.初始化协议处理器(不同序列化方式，协议处理器应不同)
     * 2.初始化客户端管理中心负责客户端的维护，这里应该依赖分流策略，如果依据指定字段分流需提前指定分流器
     */
    public void init() {
        if (manager == null) {
            synchronized (SmartClient.class) {
                if (manager == null) {
                    this.processer = new ProtocolProcesserImpl(this.conf);
                    this.manager = new ClientManager(processer, this.conf);
                    this.manager.initCluster();
                    RpcClientProxy proxy = new RpcClientProxy(manager);
                    this.client = (T) proxy.getProxy(this.conf.getInterfaze());
                }
            }
        }
    }

    /**
     * 阻塞的客户端
     *
     * @return T
     */
    public T getClient() {
        if (manager == null) {
            throw new IllegalStateException("Uninitialized SmartClient class.");
        }
        return this.client;
    }

    public T getClient(String hashkey) {
        if (manager == null) {
            throw new IllegalStateException("Uninitialized SmartClient class.");
        }
        RequestContext requestContext = RequestContext.getContext();
        requestContext.setDispachKey(hashkey);
        return this.client;
    }
}
