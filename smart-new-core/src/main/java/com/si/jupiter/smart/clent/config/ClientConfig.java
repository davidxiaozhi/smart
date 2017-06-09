package com.si.jupiter.smart.clent.config;

import com.si.jupiter.smart.clent.ClusterManagerType;
import com.si.jupiter.smart.network.SerializableEnum;
import com.si.jupiter.smart.route.RouteEnum;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:10
 */
public class ClientConfig<T> {
    private String host;
    private int connectTimeout;
    private int timeout = 1000;
    private Class<T> interfaze;//服务接口类
    private String version;//服务版本
    private SerializableEnum serializeType = SerializableEnum.PROTOBUF;//序列化方式
    private int workThreadSize = 4;
    private int nettyBossThreadSize = 1;
    private RouteEnum route = RouteEnum.ROUND;//路由方式
    private ClusterManagerType clusterManagerType=ClusterManagerType.Local;
    private ClientConfigOption clusterManagerConfig = new ClientConfigOption();

    public <C> ClientConfigOption configOption(ConfigOption<C> option,C value){
        return clusterManagerConfig.option(option,value);
    }
    public String getHost() {
        return host;
    }

    /**
     * 服务端hosts
     *
     * @param host 如：192.168.1.13:5555;192.168.1.14:5555
     * @return
     */
    public ClientConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public ClientConfig setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ClientConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ClientConfig setSerializeType(SerializableEnum serializeType) {
        this.serializeType = serializeType;
        return this;
    }

    public int getWorkThreadSize() {
        return workThreadSize;
    }

    public ClientConfig setWorkThreadSize(int workThreadSize) {
        this.workThreadSize = workThreadSize;
        return this;
    }

    public Class<T> getInterfaze() {
        return interfaze;
    }

    public ClientConfig setInterfaze(Class<T> interfaze) {
        this.interfaze = interfaze;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public SerializableEnum getSerializeType() {
        return serializeType;
    }

    public int getNettyBossThreadSize() {
        return nettyBossThreadSize;
    }

    public ClientConfig setNettyBossThreadSize(int nettyBossThreadSize) {
        this.nettyBossThreadSize = nettyBossThreadSize;
        return this;
    }

    public RouteEnum getRoute() {
        return route;
    }

    public ClientConfig setRoute(RouteEnum route) {
        this.route = route;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ClientConfig setVersion(String version) {
        this.version = version;
        return this;
    }

    public ClusterManagerType getClusterManagerType() {
        return clusterManagerType;
    }
}
