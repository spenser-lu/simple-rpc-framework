package com.luxiuyang.rpc_common.register;


public class ServerInstance {
    private String host;
    private int port;
    private int weight = 1;

    public ServerInstance() {
    }

    public ServerInstance(String host, int port, int weight) {
        this.host = host;
        this.port = port;
        this.weight = weight;
    }

    public String getHost() {
        return host;
    }

    public ServerInstance setHost(String host) {
        this.host = host;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerInstance setPort(int port) {
        this.port = port;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public ServerInstance setWeight(int weight) {
        this.weight = weight;
        return this;
    }
}
