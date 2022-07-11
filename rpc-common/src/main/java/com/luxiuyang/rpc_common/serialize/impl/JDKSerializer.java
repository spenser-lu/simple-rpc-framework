package com.luxiuyang.rpc_common.serialize.impl;

import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_common.serialize.SerializerCode;

import java.io.*;


public class JDKSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        byte[] res = null;
        try{
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            res = byteArrayOutputStream.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(byteArrayOutputStream, objectOutputStream);
        }
        return res;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        Object obj = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            obj = objectInputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(byteArrayInputStream, objectInputStream);
        }
        return obj;
    }

    @Override
    public Integer getCode() {
        return SerializerCode.JDK.getCode();
    }

    private void close(Closeable... objs) {
        for (Closeable obj : objs) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
