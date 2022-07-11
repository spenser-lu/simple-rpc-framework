package com.luxiuyang.rpc_client.config;

import com.luxiuyang.rpc_client.annotation.Reference;
import com.luxiuyang.rpc_common.util.ReflectUtils;
import java.lang.reflect.Field;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

@Configuration
public class ServiceBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ServiceBeanDefinitionRegistry.class);
    private ApplicationContext context;

    public ServiceBeanDefinitionRegistry() {
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        String scanPackage = context.getEnvironment().getProperty("rpc-client.base-package");
        if(scanPackage == null) throw new RuntimeException("未配置base-package");
        Set<Class<?>> classes = ReflectUtils.getClasses(scanPackage);
        log.info("扫描包 " + scanPackage + " 准备注入动态代理对象");
        for(Class<?> beanClass : classes) {
            Field[] fields = beanClass.getDeclaredFields();
            for(int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Reference annotation = AnnotationUtils.getAnnotation(field, Reference.class);
                if (annotation != null) {
                    Class<?> serviceClass = field.getType();
                    if (!beanDefinitionRegistry.containsBeanDefinition(serviceClass.getSimpleName())) {
                        register(serviceClass, beanDefinitionRegistry);
                    }
                }
            }
        }

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void register(Class<?> beanClass, BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        GenericBeanDefinition definition = (GenericBeanDefinition)builder.getRawBeanDefinition();
        definition.setBeanClass(ServiceFactoryBean.class);
        definition.getPropertyValues().add("serviceClass", beanClass);
        definition.getPropertyValues().add("context", context);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionRegistry.registerBeanDefinition(beanClass.getSimpleName(), definition);
        log.info(beanClass + " 已注册容器");
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

}
