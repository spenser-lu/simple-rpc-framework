
package com.luxiuyang.rpc_client.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rpc-client")
@Getter
@Setter
@ToString
public class ClientProperties {
    private Integer serializerCode = 0;
    private String clientType = "Socket";
    private String basePackage;
    private String registryHost;
    private int registryPort;
    private String loadBalancer = "Random";
}
