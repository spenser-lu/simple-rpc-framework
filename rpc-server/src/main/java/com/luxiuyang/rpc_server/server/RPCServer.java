package com.luxiuyang.rpc_server.server;

import com.luxiuyang.rpc_common.register.RegisterCenter;
import com.luxiuyang.rpc_common.util.ReflectUtils;
import com.luxiuyang.rpc_server.annotation.RPCService;
import com.luxiuyang.rpc_server.config.ServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class RPCServer implements ApplicationRunner, ApplicationContextAware {

    protected ApplicationContext context;
    protected Map<String, Object> services = new ConcurrentHashMap<>();
    protected RegisterCenter registerCenter;
    protected String localhost;
    protected int port;
    protected int weight = 1;

    public RPCServer setLocalhost(String localhost) {
        this.localhost = localhost;
        return this;
    }

    public RPCServer setPort(int port) {
        this.port = port;
        return this;
    }

    public RPCServer setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    protected void registerAll() {
        String packageName = context.getEnvironment().getProperty("rpc-server.base-package");
        registerCenter = context.getBean(RegisterCenter.class);
        if (localhost == null) {
            try {
                localhost = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {

            }
        }
        if (localhost == null) throw new RuntimeException("获取本机地址异常！");
        if (registerCenter == null) throw new RuntimeException("未配置注册中心");
        if (packageName == null) throw new RuntimeException("未配置base-package");

        log.info("扫描包 " + packageName + " 开始注册服务");
        Set<Class<?>> classes = ReflectUtils.getClassesByAnnotation(packageName, RPCService.class);
        for (Class<?> clazz : classes) {
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
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    public abstract void start();

    private void register(String serviceName, Object service) {
        this.services.put(serviceName, service);
        boolean b = registerCenter.register(serviceName, localhost, port, weight);
        if(b)
            log.info(serviceName + " 已注册");
        else{
            log.warn(serviceName + " 注册失败");
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        this.registerAll();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clearAllServices();
        }));
        this.start();
    }

    private void clearAllServices(){
        for (String serviceName : services.keySet()) {
            boolean b = registerCenter.deRegister(serviceName, localhost, port);
            if(b) log.info(serviceName + " 已注销");
            else log.warn(serviceName + " 注销失败");
        }
        log.info("所有服务已注销");
    }

}
