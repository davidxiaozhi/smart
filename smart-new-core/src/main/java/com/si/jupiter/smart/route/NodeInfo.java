package com.si.jupiter.smart.route;

/**
 * Author: lizhipeng
 * Date: 2017/01/09 10:46
 */
public class NodeInfo {
    private String ip;
    private int port;
    private int weight;//权重
    private String cluster;//服务所属集群

    public NodeInfo() {
    }

    public NodeInfo(String ip, int port) {
        this(ip, port, "");
    }

    public NodeInfo(String ip, int port, String cluster) {
        this(ip, port, cluster, 1);
    }

    public NodeInfo(String ip, int port, String cluster, int weight) {
        this.ip = ip;
        this.port = port;
        this.cluster = cluster;
        this.weight = weight;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPath() {
        return this.ip + ":" + this.port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (o instanceof NodeInfo) {
            NodeInfo nodeInfo = (NodeInfo) o;
            if (port != nodeInfo.port) {
                return false;
            }
            return !(ip != null ? !ip.equals(nodeInfo.ip) : nodeInfo.ip != null);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", weight=" + weight +
                ", cluster='" + cluster + '\'' +
                '}';
    }
}
