package com.luxiuyang.rpc_client.client;

import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;

public interface RPCClient {
    RPCResponse send(RPCRequest request);
}
