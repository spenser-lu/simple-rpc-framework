package com.luxiuyang.rpc_client.client.impl;

import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_common.MyNettyDecoder;
import com.luxiuyang.rpc_common.MyNettyEncoder;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import com.luxiuyang.rpc_common.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class NettyRPCClient implements RPCClient {
    private String host;
    private Integer port;
    private Integer serializerCode;
    private Bootstrap bootstrap;

    public NettyRPCClient(String host, Integer port, Integer serializerCode) {
        this.host = host;
        this.port = port;
        this.serializerCode = serializerCode;
        this.init();
    }

    public void init() {
        this.bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        this.bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(20971520, 12, 4, 0, 0));
                        pipeline.addLast(new MyNettyEncoder(Serializer.getByCode(serializerCode)))
                                .addLast(new MyNettyDecoder())
                                .addLast(new NettyClientHandler());
                    }
                });
    }

    public RPCResponse send(RPCRequest request) {
        try {
            ChannelFuture future = this.bootstrap.connect(new InetSocketAddress(this.host, this.port)).sync();
            Channel channel = future.channel();
            if (channel != null) {
                channel.writeAndFlush(request).addListener((future1) -> {
                    if (!future1.isSuccess()) {
                        log.error("请求 " + request + " 发送失败！");
                        future.channel().close();
                    }

                });
                channel.closeFuture().sync();
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
                RPCResponse response = channel.attr(key).get();
                return response;
            }
        } catch (InterruptedException var6) {
            log.error("客户端发送消息失败!");
            var6.printStackTrace();
        }

        return null;
    }
}
