package com.luxiuyang.rpc_common;

import com.luxiuyang.rpc_common.serialize.Serializer;

import java.io.*;

public class MySocketDecoder {

    Serializer[] serializers = new Serializer[5];

    public Object decode(byte[] bytes){
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        Object o = decode(bais);
        try {
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    public Object decode(InputStream inputStream){
        try {
            byte[] b = new byte[4];
            inputStream.read(b);
//            System.out.println(SocketUtils.bytes2Int(b));
            if(SocketUtils.bytes2Int(b) != MySocketEncoder.MAGIC_NUM){
                throw new IllegalArgumentException("不识别的协议包");
            }
            inputStream.read(b);
            int code = SocketUtils.bytes2Int(b);
            if(code != RPCRequest.getCode() && code != RPCResponse.getCode()){
                throw new IllegalArgumentException("既不是请求包也不是响应包");
            }
            inputStream.read(b);
            int serializerCode = SocketUtils.bytes2Int(b);
            if(serializers[serializerCode] == null)
                serializers[serializerCode] = Serializer.getByCode(serializerCode);
            Serializer serializer = serializers[serializerCode];
            inputStream.read(b);
            int length = SocketUtils.bytes2Int(b);
            byte[] objBytes = new byte[length];
            inputStream.read(objBytes);
            Object obj = serializer.deserialize(objBytes);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
