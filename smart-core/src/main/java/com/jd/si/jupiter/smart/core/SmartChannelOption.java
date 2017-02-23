package com.jd.si.jupiter.smart.core;

import io.netty.channel.ChannelOption;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  16-11-9 下午4:19
 * Last Update:  16-11-9 下午4:19
 */
public class SmartChannelOption<T> {
    private final ChannelOption<T> option;
    private final T value;

    public SmartChannelOption(ChannelOption<T> option, T value) {
        this.option = option;
        this.value = value;
    }

    public ChannelOption<T> getOption() {
        return option;
    }

    public T getValue() {
        return value;
    }
}
