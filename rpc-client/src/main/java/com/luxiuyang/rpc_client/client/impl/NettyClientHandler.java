package com.luxiuyang.rpc_client.client.impl;

import com.luxiuyang.rpc_common.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class NettyClientHandler extends SimpleChannelInboundHandler<RPCResponse> {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);
    private final ConcurrentHashMap<String, CompletableFuture<RPCResponse>> taskMap;

    public NettyClientHandler(ConcurrentHashMap<String, CompletableFuture<RPCResponse>> map) {
        this.taskMap = map;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCResponse rpcResponse) throws Exception {
//        try {
//            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
//            channelHandlerContext.channel().attr(key).set(rpcResponse);
//            channelHandlerContext.close();
//        } finally {
//            ReferenceCountUtil.release(rpcResponse);
//        }
        String id = rpcResponse.getId();
        CompletableFuture<RPCResponse> completableFuture = taskMap.get(id);
        completableFuture.complete(rpcResponse);

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
