package com.si.jupiter.smart.resource;

public interface ResourceClose<V>{
        boolean close(V resource);
    }