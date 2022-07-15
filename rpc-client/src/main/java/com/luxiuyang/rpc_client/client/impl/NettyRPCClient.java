package com.luxiuyang.rpc_client.client.impl;

import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_client.loadbalancer.LoadBalancer;
import com.luxiuyang.rpc_common.MyNettyDecoder;
import com.luxiuyang.rpc_common.MyNettyEncoder;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import com.luxiuyang.rpc_common.serialize.Serializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

@Slf4j
@NoArgsConstructor
@Getter
@Setter
public class NettyRPCClient implements RPCClient {

    private Integer serializerCode;
    private Bootstrap bootstrap;
    private LoadBalancer loadBalancer;
    private ConcurrentHashMap<InetSocketAddress, Channel> channelMap;
    private ConcurrentHashMap<String, CompletableFuture<RPCResponse>> taskMap;

    public NettyRPCClient(Integer serializerCode, LoadBalancer loadBalancer) {
        this.serializerCode = serializerCode;
        this.loadBalancer = loadBalancer;
        channelMap = new ConcurrentHashMap<>();
        taskMap = new ConcurrentHashMap<>();
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
                                .addLast(new NettyClientHandler(taskMap));
                    }
                }).option(ChannelOption.SO_KEEPALIVE, true);
    }

    public RPCResponse send(RPCRequest request){
        String id = UUID.randomUUID().toString();
        request.setId(id);
        taskMap.put(id, new CompletableFuture<>());
        RPCResponse response = send(request, 5);
        taskMap.remove(id);
        return response;
    }

    public RPCResponse send(RPCRequest request, int count) {
        if(count <= 0) return null;
        InetSocketAddress address = loadBalancer.select(request.getInterfaceName());
        Channel channel = getChannel(address, 5);
        if(channel == null) return null;
        RPCResponse response = doSend(request, channel);
        if(response == null){
            channelMap.remove(address);
            log.warn("客户端发送消息失败!, 重试中，剩余次数: " + (count - 1));
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            send(request, count - 1);
        }
        return response;
    }

    private RPCResponse doSend(RPCRequest request, Channel channel){
        try {
            if (channel != null) {
                channel.writeAndFlush(request).addListener((future) -> {
                    if (!future.isSuccess()) {
                        log.error("请求 " + request + " 发送失败！");
                        channel.close();
                    }
                });
                CompletableFuture<RPCResponse> future = taskMap.get(request.getId());
                RPCResponse response = future.get(5, TimeUnit.SECONDS);
                return response;
            }
        }catch (TimeoutException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            channel.close();
        }
        return null;
    }

    private Channel getChannel(InetSocketAddress address, int count){
        if(count <= 0) return null;
        Channel channel = channelMap.get(address);
        if(channel == null || !channel.isActive() || !channel.isOpen()){
            try {
                channel = bootstrap.connect(address).sync().channel();
                channelMap.put(address, channel);
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("客户端连接失败，重试中");
                channelMap.remove(address);
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                return getChannel(address, count - 1);
            }
        }
        return channel;
    }
}
