package com.si.jupiter.smart.resource;

import com.google.common.collect.ImmutableMap;

import java.util.Map;


public class ResourceConfig {
    private final ImmutableMap<String,String> config;

    public ResourceConfig(Map<String, String> config) {
        this.config = ImmutableMap.copyOf(config);
    }

    public final ImmutableMap<String, String> getConfig() {
        return config;
    }
    public final String getByKey(String key) {
        return  config.get(key);
    }
}
