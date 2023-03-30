package com.wwm.nettycommon.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ObjDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(ProtostuffUtil.deserialize(data, genericClass));
       // ctx.fireChannelRead(out);
    }

    public ObjDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

}