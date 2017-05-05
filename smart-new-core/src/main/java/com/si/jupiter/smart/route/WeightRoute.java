package com.si.jupiter.smart.route;

import com.si.jupiter.smart.commons.MurmurHash;
import io.netty.channel.Channel;

import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Author: lizhipeng
 * Date: 2017/01/09 18:18
 */
public class WeightRoute extends RpcRoute {
    private TreeMap<Long, String> nodes = new TreeMap<>();
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    private Random random = new Random();

    public Channel getServer() {
        SortedMap<Long, String> tail = nodes.tailMap(random.nextLong());
        String ni;
        if (tail.isEmpty()) {
            ni = nodes.get(nodes.firstKey());
        } else {
            ni = tail.get(tail.firstKey());
        }
        Channel channel = serverNodes.get(ni);//channel有可能是空值，remove时没有操作完成
        if (channel == null) {
            lock.readLock().lock();
            try {
                tail = nodes.tailMap(random.nextLong());
                if (tail.isEmpty()) {
                    ni = nodes.get(nodes.firstKey());
                } else {
                    ni = tail.get(tail.firstKey());
                }
                channel = serverNodes.get(ni);
            } finally {
                lock.readLock().unlock();
            }
        }
        return channel;
    }

    @Override
    public boolean addServerNode(NodeInfo node, Channel channel) {
        int w = 160 * node.getWeight();
        String key = node.getPath();
        TreeMap<Long, String> newMap = new TreeMap<>();
        lock.writeLock().lock();
        try {
            newMap.putAll(nodes);
            for (int i = 0; i < w; i++) {
                key = key + "-node-" + i;
                newMap.put(MurmurHash.hash64A(key.getBytes(), key.length()), key);
            }
            serverNodes.put(key, channel);
            nodes = newMap;
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public Channel getServer(String key) {
        Channel channel = serverNodes.get(key);//channel有可能是空值，remove时没有操作完成
        if (channel == null) {
            lock.readLock().lock();
            try {
                channel = serverNodes.get(key);
            } finally {
                lock.readLock().unlock();
            }
        }
        return channel;
    }

    @Override
    public boolean removeServerNode(String key) {
        boolean res = false;
        serverNodes.remove(key);
        TreeMap<Long, String> newMap = new TreeMap<>();
        lock.writeLock().lock();
        try {
            Iterator<Long> it = nodes.keySet().iterator();
            while (it.hasNext()) {
                Long key_ = it.next();
                String value = nodes.get(key_);
                if (!key.equals(value)) {
                    newMap.put(key_, value);
                }
            }
            nodes = newMap;
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }

    @Override
    public int size() {
        return serverNodes.size();
    }
}