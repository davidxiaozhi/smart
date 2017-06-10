package com.si.jupiter.smart.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 18:48
 */
public class FuturesManager {
    private static Map<Integer, RpcFuture> msgManager = new ConcurrentHashMap<Integer, RpcFuture>(128);

    /**
     * 设置等待队列
     *
     * @param seq 等待序号
     */
    public static RpcFuture setSeq(int seq, RpcFuture<RpcResult> future) {
        msgManager.put(seq, future);
        return future;
    }

    /**
     * 服务端返回结果后，释放客户端
     *
     * @param seq    包序号
     * @param result 服务端返回的结果
     */
    public static void release(int seq, RpcResult result) {
        RpcFuture future = msgManager.remove(seq);
        if (future != null) {
            future.responseReceived(result);
        }
    }

    /**
     * 删除队列
     *
     * @param seq 包序号
     */
    public static void remove(int seq) {
        msgManager.remove(seq);
    }

    /**
     * 通过定时器清除超时的future
     */
    static {
        Timer timer = new Timer("smart-response-timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Iterator<Map.Entry<Integer, RpcFuture>> it = msgManager.entrySet().iterator();
                while (it.hasNext()) {
                    RpcFuture rpcFuture = it.next().getValue();
                    if (rpcFuture.getInvokerTimeout() > 0 && System.currentTimeMillis() - rpcFuture.sendTime > rpcFuture.getInvokerTimeout()) {
                        it.remove();
                    }
                }
            }
        }, 5000, 3000);
    }
}
