package com.si.jupiter.smart.network.netty;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public class NettyDebugHandler extends ChannelDuplexHandler {
    public final String name;
    public final boolean isPrint;

    public NettyDebugHandler(String name, boolean isPrint) {
        super();
        this.name = name;
        this.isPrint = isPrint;
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise future) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = bind");
        }
        super.bind(ctx, localAddress, future);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise future) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = connect");
        }
        super.connect(ctx, remoteAddress, localAddress, future);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = disconnect");
        }
        super.disconnect(ctx, future);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = close");
        }
        super.close(ctx, future);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = deregister");
        }
        super.deregister(ctx, future);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = read");
        }
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = write");
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = flush");
        }
        super.flush(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelRegistered");
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelUnregistered");
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelActive");
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelInactive");
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelRead");
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelReadComplete");
        }
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = userEventTriggered");
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (isPrint) {
            System.out.println("debug(" + this.name + "):event = channelWritabilityChanged");
        }
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        //super.exceptionCaught(ctx, cause);
    }
}
