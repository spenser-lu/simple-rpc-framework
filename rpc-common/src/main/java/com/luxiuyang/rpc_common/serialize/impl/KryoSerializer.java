//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luxiuyang.rpc_common.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.luxiuyang.rpc_common.serialize.Serializer;
import com.luxiuyang.rpc_common.serialize.SerializerCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

public class KryoSerializer implements Serializer {
    ThreadLocal<Kryo> threadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.setReferences(false);
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    public KryoSerializer() {
    }

    public byte[] serialize(Object obj) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Output output = new Output(stream, 50000);

        byte[] res;
        try {
            Kryo kryo = this.threadLocal.get();
            kryo.writeClassAndObject(output, obj);
            this.threadLocal.remove();
            output.flush();
            res = stream.toByteArray();
        } finally {
            this.close(stream, output);
        }

        return res;
    }

    public Object deserialize(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            Input input = new Input(stream, 50000);

            Object obj;
            try {
                Kryo kryo = this.threadLocal.get();
                Object o = kryo.readClassAndObject(input);
                this.threadLocal.remove();
                obj = o;
            } finally {
                this.close(stream, input);
            }

            return obj;
        } else {
            return null;
        }
    }

    public Integer getCode() {
        return SerializerCode.KRYO.getCode();
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
