//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luxiuyang.rpc_common;

import java.io.Serializable;
import java.util.Arrays;

public class RPCRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public RPCRequest setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
        return this;
    }

    public RPCRequest setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public RPCRequest setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
        return this;
    }

    public RPCRequest setParams(Object[] params) {
        this.params = params;
        return this;
    }

    public static int getCode() {
        return 0;
    }

    public RPCRequest() {
    }

    public String toString() {
        return "RPCRequest(interfaceName=" + this.getInterfaceName() + ", methodName=" + this.getMethodName() + ", paramTypes=" + Arrays.deepToString(this.getParamTypes()) + ", params=" + Arrays.deepToString(this.getParams()) + ")";
    }

    public String getInterfaceName() {
        return this.interfaceName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public Class<?>[] getParamTypes() {
        return this.paramTypes;
    }

    public Object[] getParams() {
        return this.params;
    }
}
