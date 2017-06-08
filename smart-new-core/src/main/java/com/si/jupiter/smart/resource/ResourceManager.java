package com.si.jupiter.smart.resource;

public class ResourceManager {
    private final static ResourceBuilder builder = new ResourceBuilder();

    public static ResourceBuilder getResoureBuilder(){
        return  builder;
    }

    public static  <T> T getResource(ResourceOption<T> resourceKey){
        return (T) builder.getResource(resourceKey);
    }

}
