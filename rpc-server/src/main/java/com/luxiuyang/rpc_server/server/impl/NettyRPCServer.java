
package com.luxiuyang.rpc_server.server.impl;

import com.luxiuyang.rpc_common.MyNettyDecoder;
import com.luxiuyang.rpc_common.MyNettyEncoder;
import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_common.util.ReflectUtils;
import com.luxiuyang.rpc_server.annotation.RPCService;
import com.luxiuyang.rpc_server.server.RPCServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NettyRPCServer extends RPCServer {

    private Integer serializerCode;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(20971520,
                                    12, 4, 0, 0));
                            pipeline.addLast(new MyNettyDecoder());
                            pipeline.addLast(new MyNettyEncoder(Serializer.getByCode(serializerCode)));
                            pipeline.addLast(new NettyServerHandler(services));
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(this.port).sync();
            log.info("Netty 服务器已启动, 端口: " + port);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
