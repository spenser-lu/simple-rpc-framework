package com.luxiuyang.rpc_client.loadbalancer.impl;


import com.luxiuyang.rpc_client.loadbalancer.LoadBalancer;
import com.luxiuyang.rpc_common.register.RegisterCenter;
import com.luxiuyang.rpc_common.register.ServerInstance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RandomLoadbalancer implements LoadBalancer {

    private RegisterCenter registry;
    private Random random;

    public RandomLoadbalancer(RegisterCenter registerCenter){
        registry = registerCenter;
        random = new Random();
    }

    @Override
    public InetSocketAddress select(String serviceName) {
        List<ServerInstance> instances = registry.getService(serviceName);
        if(instances.size() == 1) return getAddress(instances.get(0));
        int totalWeight = 0;
        for (ServerInstance instance : instances) {
            totalWeight += instance.getWeight();
        }
        int i = random.nextInt(totalWeight);
        ServerInstance instance = null;
        for (ServerInstance serverInstance : instances) {
            if(i < serverInstance.getWeight()){
                instance = serverInstance;
                break;
            }
            i -= serverInstance.getWeight();
        }
        return getAddress(instance);
    }

    private InetSocketAddress getAddress(ServerInstance instance){
        return new InetSocketAddress(instance.getHost(), instance.getPort());
    }


}
