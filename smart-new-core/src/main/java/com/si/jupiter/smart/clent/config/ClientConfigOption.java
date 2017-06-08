package com.si.jupiter.smart.clent.config;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-19 上午11:00
 * Last Update:  17-5-19 上午11:00
 */
public class ClientConfigOption {
    private final Map<ConfigOption<?>,Object> confgOptions = new ConcurrentHashMap<ConfigOption<?>,Object>();

    public <T> ClientConfigOption option(ConfigOption<T> option, T value) {
        if (option == null) {
            throw new NullPointerException("option");
        }
        if (value == null) {
            confgOptions.remove(option);
        } else {
            confgOptions.put(option, value);
        }
        return this;
    }

    public <T> T value(ConfigOption<T> option) {
        if (option == null) {
            throw new NullPointerException("option");
        }

        return (T) confgOptions.get(option);
    }

}
