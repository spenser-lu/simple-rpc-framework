package com.luxiuyang.rpc_client.client.impl;


import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_common.MySocketDecoder;
import com.luxiuyang.rpc_common.MySocketEncoder;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import com.luxiuyang.rpc_common.serialize.Serializer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class SocketRPCClient implements RPCClient {

    private String serverHost;
    private Integer serverPort;
    private Integer serializerCode;
    private MySocketDecoder decoder;
    private MySocketEncoder encoder;
    private InetSocketAddress address;

    public SocketRPCClient(String serverHost, Integer serverPort, Integer serializerCode) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serializerCode = serializerCode;
        this.address = new InetSocketAddress(serverHost, serverPort);
        this.decoder = new MySocketDecoder();
        Serializer serializer = Serializer.getByCode(serializerCode);
        this.encoder = new MySocketEncoder(serializer);
    }

    @Override
    public RPCResponse send(RPCRequest request) {
        Socket socket = new Socket();
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            connect(socket, address, 5);
            outputStream = socket.getOutputStream();
            byte[] bytes = encoder.encode(request);
            outputStream.write(bytes);
            outputStream.flush();
            inputStream = socket.getInputStream();
            RPCResponse response = (RPCResponse) decoder.decode(inputStream);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(socket, inputStream, outputStream);
        }
        return null;
    }

    private void connect(Socket socket, InetSocketAddress address, int count){
        if(count <= 0) {
            log.error("client 连接失败");
            return;
        }
        try {
            socket.connect(address, 2000);
        } catch (IOException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            connect(socket, address, count - 1);
        }
    }

    private void close(Closeable... objs){
        for (Closeable obj : objs) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
