package com.jd.si.jupiter.smart.handler;

import com.jd.si.jupiter.smart.core.ConnectionContextHandler;
import com.jd.si.jupiter.smart.core.NettyServerConfig;
import com.jd.si.jupiter.smart.core.ThriftServerDef;
import com.jd.si.jupiter.smart.handler.ConnectionLimiter;
import com.jd.si.jupiter.smart.handler.IdleDisconnectHandler;
import com.jd.si.jupiter.smart.handler.SmartDispatcher;
import com.jd.si.jupiter.smart.statistics.ChannelStatistics;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.thrift.protocol.TProtocolFactory;

import java.util.concurrent.TimeUnit;

/**
 * JD Recommending System API Of Root
 * Version    : 2.0
 * Author     : lizhipeng@jd.com
 * Owner      : si-infra@jd.com
 * All Rights Reserved by jd.com 2016
 * create     :  16-11-1 下午10:17
 * Last Update:  16-11-1 下午10:17
 */
public class SmartChannelInitializer extends ChannelInitializer {
    private static final int NO_WRITER_IDLE_TIMEOUT = 0;
    private static final int NO_ALL_IDLE_TIMEOUT = 0;
    private final ThriftServerDef def;
    private final NettyServerConfig config;

    public SmartChannelInitializer(ThriftServerDef def, NettyServerConfig config) {
        this.def = def;
        this.config = config;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        //链接限制链接,务必确保只初始化依次
        ConnectionLimiter connectionLimiter = new ConnectionLimiter(def.getMaxConnections());
        //网络指标统计
        ChannelStatistics channelStatistics = new ChannelStatistics();
        ChannelPipeline cp = channel.pipeline();
        //输入协议工厂
        TProtocolFactory inputProtocolFactory = def.getDuplexProtocolFactory().getInputProtocolFactory();
        //是否支持ssl应该由配置决定把
        //SmartSecurityHandlers securityHandlers = def.getSecurityFactory().getSecurityHandlers(def, nettyServerConfig);
        //包装链接的元数据信息用于整个责任链
        cp.addLast("connectionContext", new ConnectionContextHandler());
        //用于限制服务端最大的链接数目
        cp.addLast("connectionLimiter", connectionLimiter);
        cp.addLast("flush",new FlushHandler());
        //debug　 handler
        cp.addLast("debug1", new DebugHandler("debug1",false));
        //用于各种信息统计
        cp.addLast(ChannelStatistics.NAME, channelStatistics);
        //cp.addLast("encryptionHandler", securityHandlers.getEncryptionHandler());
        //thrift frame编码及解码
        cp.addLast("debug2", new DebugHandler("debug2",false));
        cp.addLast("frameCodec",
                def.getThriftFrameCodecFactory().create(def.getMaxFrameSize(), inputProtocolFactory));
        //链接状态管理
        if (def.getClientIdleTimeout() > 0) {
            // Add handlers to detect idle client connections and disconnect them
                /*cp.addLast("idleTimeoutHandler", new IdleStateHandler(
                        def.getClientIdleTimeout(),
                        NO_WRITER_IDLE_TIMEOUT,
                        NO_ALL_IDLE_TIMEOUT,
                        TimeUnit.MILLISECONDS));
                cp.addLast("idleDisconnectHandler", new IdleDisconnectHandler());*/
        }

        //cp.addLast("authHandler", securityHandlers.getAuthenticationHandler());
        //负责分发,确定由那个TProcessor处理器进行处理
        cp.addLast("debug3", new DebugHandler("debug3",false));
        cp.addLast("dispatcher", new SmartDispatcher(def, config.getTimer()));
        //日志处理
        cp.addLast("debug4", new DebugHandler("debug4",false));
        cp.addLast("exceptionLogger", new LoggingHandler());
    }
}
