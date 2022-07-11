//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.luxiuyang.rpc_common;

import com.luxiuyang.rpc_common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MyNettyEncoder extends MessageToByteEncoder {
    public static final int MAGIC_NUM = 74565;
    private Serializer serializer;

    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byteBuf.writeInt(MAGIC_NUM);
        if (o instanceof RPCRequest) {
            byteBuf.writeInt(RPCRequest.getCode());
        } else {
            byteBuf.writeInt(RPCResponse.getCode());
        }

        byteBuf.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    public MyNettyEncoder() {
    }

    public MyNettyEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    public Serializer getSerializer() {
        return this.serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
}
