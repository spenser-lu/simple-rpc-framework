package com.luxiuyang.rpc_client.config;

import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@NoArgsConstructor
public class ProxyGenerator implements InvocationHandler {
    private RPCClient client;
    private Class<?> interfaceClass;
    private ApplicationContext context;
    public ProxyGenerator(Class<?> interfaceClass, ApplicationContext context) {
        this.interfaceClass = interfaceClass;
        this.context = context;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RPCRequest request = new RPCRequest()
                .setInterfaceName(this.interfaceClass.getName())
                .setMethodName(method.getName())
                .setParamTypes(method.getParameterTypes())
                .setParams(args);
        if(this.client == null){
            this.client = context.getBean(RPCClient.class);
        }
        RPCResponse response = this.client.send(request);
        return response != null && response.isSuccess() ? response.getData() : null;
    }

    public Object getProxyInstance() {
        Object instance = Proxy.newProxyInstance(this.interfaceClass.getClassLoader(), new Class[]{this.interfaceClass}, this);
        instance = this.interfaceClass.cast(instance);
        return instance;
    }

}
