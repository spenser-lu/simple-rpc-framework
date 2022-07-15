package com.luxiuyang.rpc_client.client.impl;


import com.luxiuyang.rpc_client.client.RPCClient;
import com.luxiuyang.rpc_client.loadbalancer.LoadBalancer;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class SocketRPCClient implements RPCClient {

    private LoadBalancer loadBalancer;
    private Integer serializerCode;
    private MySocketDecoder decoder;
    private MySocketEncoder encoder;
    private ConcurrentHashMap<InetSocketAddress, Socket> map;

    public SocketRPCClient(Integer serializerCode, LoadBalancer loadBalancer) {
        this.serializerCode = serializerCode;
        this.decoder = new MySocketDecoder();
        Serializer serializer = Serializer.getByCode(serializerCode);
        this.encoder = new MySocketEncoder(serializer);
        this.loadBalancer = loadBalancer;
        map = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized RPCResponse send(RPCRequest request) {
        return send(request, 5);
    }

    private RPCResponse send(RPCRequest request, int count) {
        if(count <= 0) return null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        InetSocketAddress address = loadBalancer.select(request.getInterfaceName());
        Socket socket = map.get(address);
        if(socket == null || socket.isClosed() || !socket.isConnected()){
            log.info("新建Socket");
            socket = new Socket();
            map.put(address, socket);
        }
        try {
            if(!socket.isConnected()){
                connect(socket, address, 5);
                log.info("客户端Socket发起连接");
            }
            outputStream = socket.getOutputStream();
            byte[] bytes = encoder.encode(request);
            outputStream.write(bytes);
            outputStream.flush();
            inputStream = socket.getInputStream();
            RPCResponse response = (RPCResponse) decoder.decode(inputStream);
            return response;
        } catch (Exception e) {
//            e.printStackTrace();
            close(socket, inputStream, outputStream);
            map.remove(address);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            log.warn("请求发送失败，将重试！");
            return send(request, count - 1);
        }
    }

    private void connect(Socket socket, InetSocketAddress address, int count){
        if(count <= 0) {
            log.error("client 连接失败");
            return;
        }
        try {
            socket.connect(address, 2000);
        } catch (Exception e) {
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
            if(obj == null) continue;
            try {
                obj.close();
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }
}
