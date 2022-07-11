package com.luxiuyang.rpc_client.client.impl;

import com.luxiuyang.rpc_common.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    public NettyClientHandler() {
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) throws Exception {
        try {
            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            channelHandlerContext.channel().attr(key).set(rpcResponse);
            channelHandlerContext.close();
        } finally {
            ReferenceCountUtil.release(rpcResponse);
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
