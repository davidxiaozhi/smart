package com.si.jupiter.smart.server;

/**
 * Author: baichuan - lizhipeng
 * Date: 2017/04/18 15:36
 */
public class Provider<T> {
    private Class<T> interfaze;//服务接口class
    private T service;//服务实现类对象
    private String version = "1.0";//服务版本

    public Provider() {
    }

    public Provider(Class<T> interfaze, T service, String version) {
        this.interfaze = interfaze;
        this.service = service;
        if (version != null) {
            this.version = version;
        }
    }

    public Provider(Class<T> interfaze, T service) {
        this(interfaze, service, null);
    }

    public Class<T> getInterfaze() {
        return interfaze;
    }

    public void setInterfaze(Class<T> interfaze) {
        this.interfaze = interfaze;
    }

    public T getService() {
        return service;
    }

    public void setService(T service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "interfaze=" + interfaze +
                ", service=" + service +
                ", version='" + version + '\'' +
                '}';
    }
}
