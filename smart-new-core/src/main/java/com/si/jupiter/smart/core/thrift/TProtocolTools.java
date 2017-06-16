package com.si.jupiter.smart.core.thrift;

import com.si.jupiter.smart.clent.config.ClientConfig;
import com.si.jupiter.smart.core.NetworkProtocol;
import com.si.jupiter.smart.core.SmartRequest;
import com.si.jupiter.smart.network.SerializableEnum;
import org.apache.thrift.protocol.TProtocol;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * todo       : 基于Thrift的协议包装　泛型T为iface的类型
 * Version    : 1.0
 * Author     : lizhipeng
 * create     :  17-6-12 下午3:46
 * Last Update:  17-6-12 下午3:46
 */
public class TProtocolTools<T> {

    private final ClientConfig<T> clientConfig;

    public TProtocolTools(ClientConfig<T> clientConfig) {
        this.clientConfig = clientConfig;
    }

    public NetworkProtocol buildThriftRequestProtocol(SmartRequest smartRequest) {

        //序列化产生发送消息体二进制
        byte[] thriftRequest = thriftSerialization(smartRequest);
        NetworkProtocol protocol = new NetworkProtocol();
        protocol.setSerializeType(SerializableEnum.THRIFT.getValue());
        protocol.setSequence(smartRequest.getSeq());
        protocol.setRequestTimeout(clientConfig.getRequestTimeout());
        protocol.setContent(thriftRequest);
        return protocol;
    }

    /**
     * 序列化原生的thrift请求
     * @return
     */
    private byte[]  thriftSerialization(SmartRequest smartRequest){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
        ThriftConfig<T> thriftConfig = clientConfig.getThriftConfig();
        try {
            Constructor<? extends T> constructor = thriftConfig.getClient().getConstructor(TProtocol.class);
            TProtocol protocol = ThriftUtils.getProtocolByType(thriftConfig.getOutputProtocol(),byteArrayOutputStream);
            T client = constructor.newInstance(protocol);
            String methodName = smartRequest.getMethod().getName();
            Method send = thriftConfig.getClient().getMethod("send_"+methodName,smartRequest.getMethod().getParameterTypes());
            send.invoke(client, smartRequest.getArgs());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

}
