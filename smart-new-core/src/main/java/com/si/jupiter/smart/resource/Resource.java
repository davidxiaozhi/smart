package com.si.jupiter.smart.resource;

public class Resource<V> {
    public static final Resource EMPTY = new Resource(null);
    private final V resource;

    public Resource(V resource) {
        this.resource = resource;
    }

    public final V getValue() {
        return resource;
    }

}