package com.si.jupiter.smart.resource;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ResourceBuilder {
    private final ConcurrentHashMap<ResourceOption, ResourceDefine> resouceDefines = new ConcurrentHashMap<ResourceOption, ResourceDefine>();

    protected  ResourceBuilder(){}

    /**
     * 添加资源创建任务
     */
    public void add(ResourceDefine defineTask) {
        resouceDefines.put(defineTask.getResourceKey(), defineTask);
    }

    public synchronized void build() {
        Map<ResourceOption, Resource> tempResources = new HashMap<ResourceOption, Resource>();
        for (Map.Entry<ResourceOption, ResourceDefine> resourceDefineEntry : resouceDefines.entrySet()) {
            resourceDefineEntry.getValue().makeResource();//创建资源
        }
    }

    public synchronized List<ResourceValidResult> testResourceUsablility() {
        List<ResourceValidResult> results = new LinkedList<ResourceValidResult>();
        for (Map.Entry<ResourceOption, ResourceDefine> resourceDefineEntry : resouceDefines.entrySet()) {
            results.add(resourceDefineEntry.getValue().validResource());
        }
        return results;
    }

    public synchronized void destoryAllResource() {
        for (Map.Entry<ResourceOption, ResourceDefine> resourceDefineEntry : resouceDefines.entrySet()) {
            try {
                resourceDefineEntry.getValue().destoryResource();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public <T> T getResource(ResourceOption<T> resourceKey) {
        return (T) resouceDefines.get(resourceKey).getResource().getValue();
    }

}