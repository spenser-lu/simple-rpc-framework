package com.luxiuyang.rpc_common.serialize;

import com.luxiuyang.rpc_common.serialize.impl.HessianSerializer;
import com.luxiuyang.rpc_common.serialize.impl.JDKSerializer;
import com.luxiuyang.rpc_common.serialize.impl.KryoSerializer;

public interface Serializer {
    byte[] serialize(Object obj);

    Object deserialize(byte[] bytes);

    Integer getCode();

    static Serializer getByCode(int code) {
        switch(code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new HessianSerializer();
            case 2:
                return new JDKSerializer();
            default:
                return null;
        }
    }
}
