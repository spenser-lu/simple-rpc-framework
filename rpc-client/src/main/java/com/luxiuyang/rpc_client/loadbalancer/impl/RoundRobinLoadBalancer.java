package com.luxiuyang.rpc_client.loadbalancer.impl;

import com.luxiuyang.rpc_client.loadbalancer.LoadBalancer;
import com.luxiuyang.rpc_common.register.RegisterCenter;
import com.luxiuyang.rpc_common.register.ServerInstance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class RoundRobinLoadBalancer implements LoadBalancer {

    private RegisterCenter registry;

    private AtomicInteger num;

    public RoundRobinLoadBalancer(RegisterCenter registry){
        this.registry = registry;
        num = new AtomicInteger(-1);
    }

    @Override
    public InetSocketAddress select(String serviceName) {
        List<ServerInstance> instances = registry.getService(serviceName);
        if(instances.size() == 1) return getAddress(instances.get(0));
        int i = num.incrementAndGet();
        return getAddress(instances.get(instances.size() % i));
    }

    private InetSocketAddress getAddress(ServerInstance instance){
        return new InetSocketAddress(instance.getHost(), instance.getPort());
    }
}
