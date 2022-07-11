package com.luxiuyang.rpc_server.server;

import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import com.luxiuyang.rpc_common.util.ReflectUtils;
import com.luxiuyang.rpc_server.annotation.RPCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class RPCServer implements ApplicationRunner, ApplicationContextAware {

    protected ApplicationContext context;
    protected Map<String, Object> services = new ConcurrentHashMap<>();

    protected void registerAll() {
        String packageName = this.context.getEnvironment().getProperty("rpc-server.base-package");
        if (packageName == null) throw new RuntimeException("未配置base-package");
        log.info("扫描包 " + packageName + " 开始注册服务");
        Set<Class<?>> classes = ReflectUtils.getClassesByAnnotation(packageName, RPCService.class);
        classes.forEach((clazz) -> {
            Object obj = null;
            try {
                obj = this.context.getBean(clazz);
            } catch (Exception e) {

            }
            if (obj == null) {
                try {
                    obj = clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (obj != null) {
                Class<?>[] interfaces = clazz.getInterfaces();
                for (int i = 0; i < interfaces.length; i++) {
                    Class<?> anInterface = interfaces[i];
                    this.register(anInterface.getName(), obj);
                }
            }

        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public abstract void start();

    private void register(String serviceName, Object service) {
        this.services.put(serviceName, service);
        log.info(serviceName + " 已注册");
    }

    @Override
    public void run(ApplicationArguments args) {
        this.registerAll();
        this.start();
    }
}
