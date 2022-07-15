package com.luxiuyang.rpc_common;

import com.luxiuyang.rpc_common.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;


import java.util.List;

public class MyNettyDecoder extends ByteToMessageDecoder {

    public static final int MAGIC_NUM = MyNettyEncoder.MAGIC_NUM;
    public Serializer[] serializers = new Serializer[5];

    public MyNettyDecoder() {
    }

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magic_num = byteBuf.readInt();
        if (magic_num != MAGIC_NUM) {
            throw new RuntimeException("不识别的数据包");
        } else {
            int clazzCode = byteBuf.readInt();
            if (clazzCode != RPCRequest.getCode() && clazzCode != RPCResponse.getCode()) {
                throw new RuntimeException("既不是请求包也不是响应包");
            } else {
                int serializerCode = byteBuf.readInt();
                if (this.serializers[serializerCode] == null) {
                    this.serializers[serializerCode] = Serializer.getByCode(serializerCode);
                }

                Serializer serializer = this.serializers[serializerCode];
                int length = byteBuf.readInt();
                byte[] bytes = new byte[length];
                byteBuf.readBytes(bytes);
                Object obj = serializer.deserialize(bytes);
                list.add(obj);
            }
        }
    }
}
