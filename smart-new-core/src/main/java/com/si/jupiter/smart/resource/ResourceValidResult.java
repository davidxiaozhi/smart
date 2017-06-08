package com.si.jupiter.smart.resource;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-6-5 下午5:33
 * Last Update:  17-6-5 下午5:33
 */
public class ResourceValidResult {
    public static final ResourceOption EMPTY_OPTION = new ResourceOption("empty");
    public static final ResourceValidResult EMPTY = new ResourceValidResult(false,EMPTY_OPTION,"it is empty default!!!");
    private final boolean isUsable;
    private final String  message;

    public ResourceValidResult(boolean isUsable, ResourceOption option,String message) {
        this.isUsable = isUsable;
        this.message = "("+option.key+")"+message;
    }

    public boolean isUsable() {
        return isUsable;
    }

    public String getMessage() {
        return message;
    }
}
