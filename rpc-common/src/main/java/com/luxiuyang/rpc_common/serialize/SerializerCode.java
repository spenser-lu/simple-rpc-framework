package com.luxiuyang.rpc_common.serialize;

public enum SerializerCode {
    KRYO(0),
    HESSIAN(1),
    JDK(2);

    private final int code;

    SerializerCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
