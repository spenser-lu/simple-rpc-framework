package com.luxiuyang.rpc_common.register.impl;


import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.luxiuyang.rpc_common.register.RegisterCenter;
import com.luxiuyang.rpc_common.register.ServerInstance;

import java.util.ArrayList;
import java.util.List;

public class NacosRegistry implements RegisterCenter {

    NamingService namingService;

    public NacosRegistry(String host, int port){
        try {
            namingService = NamingFactory.createNamingService(host + ":" + port);
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ServerInstance> getService(String serviceName){
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            List<ServerInstance> serverInstances = new ArrayList<>();
            for (Instance instance : instances) {
                serverInstances.add(new ServerInstance(instance.getIp(), instance.getPort(), (int)instance.getWeight()));
            }
            return serverInstances;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean register(String serviceName, String host, int port, int weight) {
        try {
            Instance instance = new Instance();
            instance.setIp(host);
            instance.setPort(port);
            instance.setWeight((double) weight);
            namingService.registerInstance(serviceName, instance);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean register(String serviceName, String host, int port) {
        register(serviceName, host, port, 1);
        return false;
    }

    @Override
    public boolean deRegister(String serviceName, String host, int port) {
        try {
            namingService.deregisterInstance(serviceName, host, port);
            return true;
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return false;
    }

}
