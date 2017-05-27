package com.si.jupiter.smart.clent;

import com.si.jupiter.smart.clent.config.ClientConfig;

/**
 * Author: lizhipeng
 * Date: 2017/01/04 15:06
 */
public class SmartClientFactory {

    /**
     * 获得服务客户端
     *
     * @param conf 客户端配置
     * @param <T> T
     * @return T
     */
    public static <T> T getServiceConsumer(ClientConfig<T> conf) {
        SmartClient<T> client = new SmartClient<T>(conf);
        client.init();
        return client.getClient();
    }
}
