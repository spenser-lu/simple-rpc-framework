//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luxiuyang.rpc_common;

import java.io.Serializable;

public class RPCResponse implements Serializable {
    private Integer statusCode;
    private Object data;
    private String msg;

    public RPCResponse setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public RPCResponse setData(Object data) {
        this.data = data;
        return this;
    }

    public RPCResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public static RPCResponse success() {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code);
        return response;
    }

    public static RPCResponse success(Object data) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code).setData(data);
        return response;
    }

    public static RPCResponse success(Object data, String msg) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code).setData(data).setMsg(msg);
        return response;
    }

    public static RPCResponse fail(String msg) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.INVOKE_FAIL.code).setMsg(msg);
        return response;
    }

    public static int getCode() {
        return 1;
    }

    public boolean isSuccess() {
        return this.statusCode.equals(RPCResponseStatus.OK.code);
    }

    public Integer getStatusCode() {
        return this.statusCode;
    }

    public Object getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public String toString() {
        return "RPCResponse(statusCode=" + this.getStatusCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }

    public RPCResponse() {
    }
}
