package com.si.jupiter.smart.core;

import com.si.jupiter.smart.server.Provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: lizhipeng
 * Date: 2017/01/03 19:03
 * 服务接口方法映射
 */
public class ServiceMapper {
    private static Map<String, Map<String, Method>> serviceMethodMap;
    private static Map<String, Object> servicesMap;

    /**
     * 将方法进行映射
     *
     * @param providers 服务提供者
     */
    public static void init(Provider[] providers) {
        if (providers == null) {
            throw new IllegalArgumentException("Service provider can't null.");
        }
        serviceMethodMap = new HashMap<String, Map<String, Method>>(providers.length);
        servicesMap = new HashMap<String, Object>(providers.length);
        for (Provider provider : providers) {
            Method[] methods = provider.getInterfaze().getMethods();
            Map<String, Method> methodMap = new HashMap<String, Method>(methods.length);
            String serviceName = buildServiceName(provider.getInterfaze().getName(), provider.getVersion());
            serviceMethodMap.put(serviceName, methodMap);
            for (Method method : methods) {
                methodMap.put(ProtocolProcesser.buildMethodName(method), method);
            }
            servicesMap.put(serviceName, provider.getService());
        }
    }

    /**
     * 通过key获取Method对象
     *
     * @param service 服务接口名称
     * @param method  方法标识
     * @return Method
     */
    public static Method getMethod(String service, String version, String method) {
        Method m = serviceMethodMap.get(buildServiceName(service, version)).get(method);
        if (m == null) {
            throw new NullPointerException("The " + method + " method was not found in the " + service + " service, version:" + version);
        }
        return m;
    }

    /**
     * 通过接口名称获取接口实现对象
     *
     * @param service 服务名称
     * @return Object
     */
    public static Object getSerivce(String service, String version) {
        Object obj = servicesMap.get(buildServiceName(service, version));
        if (obj == null) {
            throw new NullPointerException("Not foud service:" + service + " version:" + version);
        }
        return obj;
    }

    private static String buildServiceName(String service, String version) {
        return service + ":" + version;
    }
}
