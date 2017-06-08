package com.si.jupiter.smart.clent.config;

import java.util.concurrent.atomic.AtomicLong;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-18 下午8:42
 * Last Update:  17-5-18 下午8:42
 */
public class ConfigOption<T> {
    private static final AtomicLong uniqueIdGenerator = new AtomicLong();
    private final String name;
    private final long uniquifier;
    private ConfigOption(String name){
        this.uniquifier = uniqueIdGenerator.getAndIncrement();
        this.name = name;
    }
    public static <T> ConfigOption<T> valueOf(String name) {

        return new ConfigOption<T>(name);
    }
}


