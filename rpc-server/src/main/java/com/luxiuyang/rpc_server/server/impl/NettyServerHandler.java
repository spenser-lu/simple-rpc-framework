
package com.luxiuyang.rpc_server.server.impl;

import com.luxiuyang.rpc_common.RPCRequest;
import com.luxiuyang.rpc_common.RPCResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    Map<String, Object> services;

    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest request) throws Exception {
        RPCResponse response = this.doRequest(request);
        ctx.channel().writeAndFlush(response);
    }

    private RPCResponse doRequest(RPCRequest request) {
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object service = this.services.get(interfaceName);
        Object res = null;

        try {
            Method method = service.getClass().getMethod(methodName, paramTypes);
            res = method.invoke(service, request.getParams());
            return RPCResponse.success(res);
        } catch (NoSuchMethodException e) {
            log.error("方法 " + methodName + " 不存在！");
            e.printStackTrace();
            return RPCResponse.fail("该方法不存在!");
        } catch (Exception e) {
            log.error("方法 " + methodName + " 执行失败！");
            e.printStackTrace();
            return RPCResponse.fail("执行失败!");
        }
    }

    public NettyServerHandler() {
    }

    public NettyServerHandler(Map<String, Object> services) {
        this.services = services;
    }
}
