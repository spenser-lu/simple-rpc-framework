
package com.luxiuyang.rpc_server.config;

import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_server.server.RPCServer;
import com.luxiuyang.rpc_server.server.impl.NettyRPCServer;
import com.luxiuyang.rpc_server.server.impl.SocketRPCServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ServerProperties.class})
@Slf4j
public class RPCServerConfig {
    @Autowired
    ServerProperties properties;

    public RPCServerConfig() {
    }

    @Bean
    public RPCServer rpcServer() {
        Integer port = properties.getPort();
        Integer serializerCode = properties.getSerializerCode();
        String type = properties.getServerType();
        RPCServer server = null;
        if(type.toLowerCase().equals("netty")){
            server = new NettyRPCServer(port, serializerCode);
        }else if(type.toLowerCase().equals("socket")){
            server = new SocketRPCServer(port, serializerCode);
        }else{
            throw new IllegalArgumentException("服务端类型必须为Netty或Socket");
        }
        log.info("序列化器编号: " + serializerCode + "; 0 -> Kryo, 1 -> Hessian, 2 -> JDK");
        return server;
    }
}
