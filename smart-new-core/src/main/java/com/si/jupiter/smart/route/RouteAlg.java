package com.si.jupiter.smart.route;

import com.si.jupiter.smart.channel.SmartChannel;

/**
 * todo       : todo something
 * Version    : 1.0
 * Author     : lizhipeng
 * makeResource     :  17-5-23 下午11:38
 * Last Update:  17-5-23 下午11:38
 */
public interface RouteAlg {
    SmartChannel dispatch();
    SmartChannel dispatch(String dispacherKey);
}
