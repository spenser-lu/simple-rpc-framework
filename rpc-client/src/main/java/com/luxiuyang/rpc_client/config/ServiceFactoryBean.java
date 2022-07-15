package com.luxiuyang.rpc_client.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;

@Getter
@Setter
public class ServiceFactoryBean<T> implements FactoryBean<T> {
    private Class<T> serviceClass;
    private ApplicationContext context;

    public ServiceFactoryBean() {
    }

    public T getObject() throws Exception {
        return (T)new ProxyGenerator(serviceClass, context).getProxyInstance();
    }

    @Override
    public Class<?> getObjectType() {
        return this.serviceClass;
    }

    public boolean isSingleton() {
        return true;
    }

    private String getProperty(String s) {
        return this.context.getEnvironment().getProperty(s);
    }

}
