package com.si.jupiter.smart.clent;

import com.si.jupiter.smart.channel.SmartChannel;
import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.cluster.*;
import com.si.jupiter.smart.core.*;
import com.si.jupiter.smart.network.netty.NettyClient;
import com.si.jupiter.smart.route.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:32
 * 客户端管理类，后续可扩展为集群管理
 */
public class ClientManager<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);
    private static AtomicInteger seqCount = new AtomicInteger(0);
    private ProtocolProcesserImpl processer;
    private ClientConfig config;
    private Invoker invoker;
    private NettyClient nettyClient;
    private RpcRoute route;
    private ClusterManagerTask managerTask;

    public ClientManager(ProtocolProcesserImpl processer, ClientConfig config) {
        this.processer = processer;
        this.config = config;
        invoker = new ChannelInvoker();
        nettyClient = new NettyClient(config);
        managerTask = new DefaultCMTask();
        RouteEnum routeEnum = config.getRoute();
        if(RouteEnum.CONSISTENTHASH.equals(routeEnum)){
            route = new ConsistentHashRoute(config,managerTask);
        }
        else{
            route = new RoundRoute(config,managerTask);
        }
        initCluster();

    }

    /**
     * 初始化所有客户端连接
     */
    public void initCluster() {
        String hostStr = this.config.getHost();
        if (hostStr != null) {
            String[] hosts = hostStr.split(";");
            for (String host : hosts) {
                String[] ipPort = host.trim().split(":");
                String ip = ipPort[0];
                int port = Integer.parseInt(ipPort[1]);
                ServerNode serverNode = new ServerNode(ip, port);
                serverNode.setServerStatus(ServerStatus.Serve);
                managerTask.addClusterNode(serverNode);
            }
        }
    }

    /**
     * 获取客户端连接通道
     *
     * @return SmartChannel
     * @throws InterruptedException e
     */
    public SmartChannel getChannel() throws InterruptedException {
        RequestContext requestContext = RequestContext.getContext();
        String dispatchKey = requestContext.getDispachKey();
        LOGGER.debug("-***-request-context-dispacher-key {} ",dispatchKey);
        SmartChannel smartChannel = SmartChannel.EMPTY;
        if(RouteEnum.CONSISTENTHASH.equals(config.getRoute())){
            smartChannel = this.route.dispatcher(dispatchKey);
        }
        else {
            smartChannel = route.dispatcher();
        }LOGGER.debug("-***- get the serverNode of SmartChannel is  {} ",smartChannel.getServerNode());
        return smartChannel;
    }

    /**
     * 调用远程方法
     *
     * @param method 方法对象
     * @param args   方法参数
     * @return T
     * @throws Exception e
     */
    public T invoke(String serviceName, Method method, Object[] args) throws Exception {
        int seq = createdPackageId();
        NetworkProtocol protocol = this.processer.buildRequestProtocol(new SmartRequest(serviceName, this.config.getVersion(), method, args, seq));
        RpcFuture<RpcResult> rpcFuture = new ResponseFuture<>(config.getTimeout());
        FuturesManager.setSeq(seq, rpcFuture);
        SmartChannel smartChannel = this.getChannel();
        if(!SmartChannel.EMPTY.equals(smartChannel)){
            this.invoker.invoke(smartChannel.getNettyChannel(), protocol, seq);
        }
        else{
            System.out.println("-***-the channel is empty");
        }
        RpcResult result = null;
        try {
            result = rpcFuture.get(config.getTimeout(), TimeUnit.MILLISECONDS);
        } finally {
            if (result == null) {//客户端超时
                FuturesManager.remove(protocol.getSequence());
            }
        }
        if (result != null) {
            Throwable exception = result.getException();
            if (exception != null) {//服务端发生异常
                throw new Exception("Service provider exception.", exception);
            }
            return (T) result.getValue();
        }
        return null;
    }

    /**
     * 异步获取调用结果
     *
     * @param method 方法
     * @param args   参数
     * @return RpcFuture<T>
     * @throws Exception e
     */
    public RpcFuture<T> asyncFutureInvoke(String serviceName, Method method, Object[] args) throws Exception {
        int seq = createdPackageId();
        NetworkProtocol protocol = this.processer.buildRequestProtocol(new SmartRequest(serviceName, this.config.getVersion(), method, args, seq));
        RpcFuture<RpcResult> rpcFuture = new ResponseFuture<>(config.getTimeout());
        FuturesManager.setSeq(seq, rpcFuture);
        SmartChannel smartChannel = this.getChannel();
        if(!SmartChannel.EMPTY.equals(smartChannel)){
            this.invoker.invoke(smartChannel.getNettyChannel(), protocol, seq);
        }
        else{
            System.out.println("-***-the channel is empty");
        }
        return new ResultFuture<>(rpcFuture, seq);
    }

    /**
     * 异步获取调用结果
     *
     * @param method   方法
     * @param args     参数
     * @param callback 回调函数
     * @throws Exception e
     */
    public void asyncCallbackInvoke(String serviceName, Method method, Object[] args, RpcCallback callback) throws Exception {
        int seq = createdPackageId();
        NetworkProtocol protocol = this.processer.buildRequestProtocol(new SmartRequest(serviceName, this.config.getVersion(), method, args, seq));
        RpcFuture<RpcResult> rpcFuture = new ResponseCallback(callback, config.getTimeout());
        FuturesManager.setSeq(seq, rpcFuture);
        SmartChannel smartChannel = this.getChannel();
        if(!SmartChannel.EMPTY.equals(smartChannel)){
            this.invoker.invoke(smartChannel.getNettyChannel(), protocol, seq);
        }
        else{
            System.out.println("-***-the channel is empty");
        }
    }

    /**
     * 生成一个包ID
     *
     * @return int
     */
    public int createdPackageId() {
        return seqCount.incrementAndGet();
    }
}
