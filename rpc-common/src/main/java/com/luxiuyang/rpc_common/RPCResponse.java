package com.luxiuyang.rpc_common;

import java.io.Serializable;

public class RPCResponse implements Serializable {
    private Integer statusCode;
    private Object data;
    private String msg;
    private String id;

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

    public static RPCResponse success(String id) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code).setId(id);
        return response;
    }

    public static RPCResponse success(Object data, String id) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code).setData(data).setId(id);
        return response;
    }

    public static RPCResponse success(Object data, String msg, String id) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.OK.code).setData(data).setMsg(msg).setId(id);
        return response;
    }

    public static RPCResponse fail(String msg, String id) {
        RPCResponse response = new RPCResponse();
        response.setStatusCode(RPCResponseStatus.INVOKE_FAIL.code).setMsg(msg).setId(id);
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

    public String getId() {
        return id;
    }

    public RPCResponse setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "RPCResponse{" +
                "statusCode=" + statusCode +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public RPCResponse() {
    }
}
