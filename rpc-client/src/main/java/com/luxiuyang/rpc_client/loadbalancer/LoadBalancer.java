package com.luxiuyang.rpc_client.loadbalancer;


import java.net.InetSocketAddress;
import java.util.List;

public interface LoadBalancer {

    InetSocketAddress select(String serviceName);
}
