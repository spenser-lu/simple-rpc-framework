package com.luxiuyang.rpc_server.server.impl;


import com.luxiuyang.rpc_common.MySocketDecoder;
import com.luxiuyang.rpc_common.MySocketEncoder;
import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import com.luxiuyang.rpc_common.serialize.Serializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

@AllArgsConstructor
@Slf4j
public class SocketRPCHandler implements Runnable{

    private Map<String, Object> services;
    private Socket socket;
    private MySocketEncoder encoder;
    private MySocketDecoder decoder;


    private RPCResponse handleRequest(RPCRequest request){
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object service = this.services.get(interfaceName);
        Object res = null;
        try {
            Method method = service.getClass().getMethod(methodName, paramTypes);
            res = method.invoke(service, request.getParams());
            return RPCResponse.success(res, request.getId());
        } catch (NoSuchMethodException e) {
            log.error("方法 " + methodName + " 不存在！");
            e.printStackTrace();
            return RPCResponse.fail("该方法不存在!", request.getId());
        } catch (Exception e) {
            log.error("方法 " + methodName + " 执行失败！");
            e.printStackTrace();
            return RPCResponse.fail("执行失败!", request.getId());
        }
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        log.info("Socket: " + socket.getRemoteSocketAddress() + " 已连接");
        while(!socket.isClosed() && socket.isConnected()){
            try {
                inputStream = socket.getInputStream();
                RPCRequest request = (RPCRequest) decoder.decode(inputStream);
                RPCResponse response = handleRequest(request);
                byte[] bytes = encoder.encode(response);
                outputStream = socket.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();
            } catch (Exception e) {
//                e.printStackTrace();
                close(socket, inputStream, outputStream);
                log.info("Socket: " + socket.getRemoteSocketAddress() + " 已关闭");
                break;
            }
        }


    }

    private void close(Closeable... objs){
        for (Closeable obj : objs) {
            if(obj == null) continue;
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
