
package com.luxiuyang.rpc_client.config;

import com.luxiuyang.rpc_client.client.impl.NettyRPCClient;
import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_client.client.impl.SocketRPCClient;
import com.luxiuyang.rpc_client.loadbalancer.LoadBalancer;
import com.luxiuyang.rpc_client.loadbalancer.impl.RandomLoadbalancer;
import com.luxiuyang.rpc_common.register.RegisterCenter;
import com.luxiuyang.rpc_common.register.impl.NacosRegistry;

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
    public RPCClient rpcClient(LoadBalancer loadBalancer) {
        Integer serializerCode = properties.getSerializerCode();
        String type = properties.getClientType();
        RPCClient client = null;
        if(type.toLowerCase().equals("socket")){
            client = new SocketRPCClient(serializerCode, loadBalancer);
            log.info("客户端类型: Socket" );
        }else if(type.toLowerCase().equals("netty")){
            client = new NettyRPCClient(serializerCode, loadBalancer);
            log.info("客户端类型: Netty" );
        }else{
            throw new IllegalArgumentException("客户端类型必须为Socket或Netty");
        }
        log.info("序列化器编号: " + serializerCode + "; 0 -> Kryo, 1 -> Hessian, 2 -> JDK");
        return client;
    }

    @Bean
    public LoadBalancer loadBalancer(RegisterCenter registry){
        String balancer = properties.getLoadBalancer();
        LoadBalancer loadBalancer = null;
        if(balancer.toLowerCase().equals("random")){
            loadBalancer = new RandomLoadbalancer(registry);
            log.info("已配置Random负载均衡器");
        }else if(balancer.toLowerCase().equals("roundrobin")){
            loadBalancer = new RandomLoadbalancer(registry);
            log.info("已配置RoundRobin均衡器");
        }else {
            throw new IllegalArgumentException("负载均衡器必须为Random或RoundRobin");
        }

        return loadBalancer;
    }

    @Bean
    public RegisterCenter registerCenter(){
        String registryHost = properties.getRegistryHost();
        int port = properties.getRegistryPort();
        RegisterCenter registry = new NacosRegistry(registryHost, port);
        log.info("注册中心地址 " + registryHost + ":" + port);
        return registry;
    }
}
