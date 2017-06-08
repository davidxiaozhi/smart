package com.si.jupiter.smart.pools;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-12 下午1:45
 * Last Update:  17-5-12 下午1:45
 */
public enum ShardType {
    AddNode("add"),DeleteNode("delete");
    private String type;
    ShardType(String type){
        this.type=type;
    }
    public String getValue(){
        return this.type;
    }
}
