package com.luxiuyang.rpc_server.server.impl;

import com.luxiuyang.rpc_common.MySocketDecoder;
import com.luxiuyang.rpc_common.MySocketEncoder;
import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_server.server.RPCServer;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Slf4j
public class SocketRPCServer extends RPCServer {

    private Integer port;
    private Integer serializerCode;
    private MySocketDecoder decoder;
    private MySocketEncoder encoder;

    public SocketRPCServer(Integer port, Integer serializerCode) {
        this.port = port;
        this.serializerCode = serializerCode;
        Serializer serializer = Serializer.getByCode(serializerCode);
        decoder = new MySocketDecoder();
        encoder = new MySocketEncoder(serializer);
    }

    private static ThreadPoolExecutor pool = new ThreadPoolExecutor(5,
            10,
            1, TimeUnit.DAYS,
            new ArrayBlockingQueue<>(500),
            new DefaultThreadFactory("RPC Server"),
            new ThreadPoolExecutor.DiscardPolicy());

    @Override
    public void start(){
        ServerSocket serverSocket =null;
        try {
            serverSocket = new ServerSocket(port);
            log.info("Socket 服务器已启动, 端口: " + port);
            while (true){
                Socket socket = serverSocket.accept();
                pool.submit(new SocketRPCHandler(this.services, socket, encoder, decoder));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
