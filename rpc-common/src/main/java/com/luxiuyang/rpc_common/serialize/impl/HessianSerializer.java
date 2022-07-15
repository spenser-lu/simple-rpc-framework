package com.luxiuyang.rpc_common.serialize.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_common.serialize.SerializerCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;


public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream baos = null;
        Hessian2Output h2o = null;
        byte[] res = null;
        try {
            baos = new ByteArrayOutputStream();
            h2o = new Hessian2Output(baos);
            h2o.writeObject(obj);
            h2o.flush();
            res = baos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(baos);
            try {
                h2o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        Hessian2Input h2i = null;
        Object obj = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            h2i = new Hessian2Input(bais);
            obj = h2i.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            close(bais);
            try {
                h2i.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    @Override
    public Integer getCode() {
        return SerializerCode.HESSIAN.getCode();
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
