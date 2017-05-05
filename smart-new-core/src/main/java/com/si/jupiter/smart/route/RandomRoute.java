package com.si.jupiter.smart.route;

import io.netty.channel.Channel;

import java.util.Random;

/**
 * Author: lizhipeng
 * Date: 2017/01/09 10:54
 */
public class RandomRoute extends RotationRoute {
    private Random random = new Random();

    @Override
    public Channel getServer() {
        int r = random.nextInt(1000000);
        if (serverNodes.isEmpty()) {
            return null;
        }
        int index = r % serverNodes.size();
        return nodes.get(index);
    }
}
