
package com.luxiuyang.rpc_client.config;

import com.luxiuyang.rpc_client.client.impl.NettyRPCClient;
import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_client.client.impl.SocketRPCClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ClientProperties.class})
@Slf4j
public class ClientConfig {
    @Autowired
    ClientProperties properties;

    public ClientConfig() {
    }

    @Bean
    public RPCClient rpcClient() {
        String serverHost = properties.getServerHost();
        Integer serverPort = properties.getServerPort();
        Integer serializerCode = properties.getSerializerCode();
        String type = properties.getClientType();
        RPCClient client = null;
        if(type.toLowerCase().equals("socket")){
            client = new SocketRPCClient(serverHost, serverPort, serializerCode);
            log.info("客户端类型: Socket" );
        }else if(type.toLowerCase().equals("netty")){
            client = new NettyRPCClient(serverHost, serverPort, serializerCode);
            log.info("客户端类型: Netty" );
        }else{
            throw new IllegalArgumentException("客户端类型必须为Socket或Netty");
        }
        log.info("序列化器编号: " + serializerCode + "; 0 -> Kryo, 1 -> Hessian, 2 -> JDK");
        log.info("RPC服务器地址: " + serverHost + ":" + serverPort);
        return client;
    }
}
