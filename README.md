# 轻量级RPC框架
- 封装成SpringBoot-Starter，简化配置，开箱即用
- 支持全注解式开发，客户端使用JDK动态代理，并支持自动注入Spring容器，使用方便
- 网络通信支持Netty和Java Socket，通过自定义通信格式，防止粘包和半包问题
- 序列化方式可配置，支持Kyro、Hessian和JDK序列化，客户端和服务端可任意搭配使用
- 本网站的Redis缓存部分即采用此框架进行开发

## 使用

### 服务端
首先创建一个SpringBoot项目，并且引入rpc-server依赖和公共APi依赖：
```xml
<dependency>
       <groupId>com.luxiuyang</groupId>
       <artifactId>rpc-server</artifactId>
       <version>1.0</version>
</dependency>
<dependency>
       //公共API依赖
</dependency>
```
在配置文件（application.yml或application.properties）中配置必要的参数：
```k
rpc-server:
  # 服务器的监听端口
  port: 10010

  # 序列化器编号， 0 -> Kyro, 1 -> Hessian, 2 -> JDK，默认为0
  serializer-code: 1

  # 注解扫描的包, 用于扫描@RPCService注解，实现服务自动注册
  base-package: com.xxx.xxx

  # 服务端的类型，可以是Netty或Socket，默认是后者
  server-type: Netty
```
在服务端编写接口的实现类，并用`@RPCService`注解标注：
```java
@RPCService
public class PersonImpl implements Person{

    @Override
    public String hello(String name){
        return "Hello " + name;
    }
}
```
启动Spring容器，在控制台会打印一些配置信息，并显示`服务器已启动`：

![屏幕截图 20220711 163617.jpg](https://www.luxiuyang.online:443/file/blog/1/ndTLbp9iKO.png)

### 客户端
首先创建一个SpringBoot项目，并且引入rpc-client依赖和公共APi依赖：
```xml
<dependency>
       <groupId>com.luxiuyang</groupId>
       <artifactId>rpc-server</artifactId>
       <version>1.0</version>
</dependency>
<dependency>
       //公共API依赖
</dependency>
```
在配置文件（application.yml或application.properties）中配置必要的参数：
```k
rpc-client:
  # 序列化器编号， 0 -> Kyro, 1 -> Hessian, 2 -> JDK，默认为0
  serializer-code: 1

  # 服务端的地址
  server-host: localhost

  # 服务端的端口号
  server-port: 10010

  # 注解扫描的包, 用于扫描@Reference注解，实现动态代理对象自动注入
  base-package: com.xxx.xxx

  # 客户端的类型，可以是Netty或Socket，默认是后者
  client-type: Socket
```
`serializer-code`和`client-type`无需和服务端保持一致，可以任意搭配使用
***
接下来编写相应的类，可以用`@Reference`注解直接注入接口，框架会自动帮我们完成动态代理对象注入，可以像使用本地方法一样来使用它：
```java
@RestController
public class HelloController{
 
    @Reference
    private Person;

    @GetMapping("/hello")
    public String hello(){
        Person.hello("Luxiuyang");
    }
}
```

