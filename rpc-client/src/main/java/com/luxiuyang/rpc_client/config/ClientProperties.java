//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

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
    private Integer serverPort = 10010;
    private String serverHost = "localhost";
    private Integer serializerCode = 0;
    private String clientType = "Socket";
    private String basePackage;
}
