package com.luxiuyang.rpc_server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

@ConfigurationProperties(prefix = "rpc-server")
@Getter
@Setter
public class ServerProperties {
    private Integer port = 10010;
    private Integer SerializerCode = 0;
    private String basePackage;
    private String serverType = "Socket";
    private String hostAddress = "localhost";
    private String registryHost;
    private int registryPort;
    private int weight = 1;
}
