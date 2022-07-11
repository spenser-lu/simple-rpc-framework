//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luxiuyang.rpc_common;

public enum RPCResponseStatus {
    OK(200),
    NO_SUCH_METHOD(404),
    INVALID_PARAMETERS(400),
    INVOKE_FAIL(500);

    public int code;

    RPCResponseStatus(int code) {
        this.code = code;
    }
}
