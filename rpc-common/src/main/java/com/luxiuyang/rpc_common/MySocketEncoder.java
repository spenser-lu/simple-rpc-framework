package com.luxiuyang.rpc_common;

import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_common.serialize.impl.KryoSerializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MySocketEncoder {
    public static final int MAGIC_NUM = MyNettyEncoder.MAGIC_NUM;
    Serializer serializer;

    public MySocketEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    public MySocketEncoder() {
    }

    public byte[] encode(Object o){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(SocketUtils.int2Bytes(MAGIC_NUM));
            if(o instanceof RPCRequest){
                baos.write(SocketUtils.int2Bytes(RPCRequest.getCode()));
            }else if(o instanceof RPCResponse){
                baos.write(SocketUtils.int2Bytes(RPCResponse.getCode()));
            }else {
                throw new IllegalArgumentException("不识别的包！");
            }
            baos.write(SocketUtils.int2Bytes(serializer.getCode()));
            byte[] bytes = serializer.serialize(o);
            baos.write(SocketUtils.int2Bytes(bytes.length));
            baos.write(bytes);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
