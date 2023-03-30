package com.wwm.nettycommon.nettyClient;

import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.protocol.ObjDecoder;
import com.wwm.nettycommon.protocol.ObjEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class MyClientInit extends ChannelInitializer<SocketChannel> {
    private final MyClientHandler myClientHandler = new MyClientHandler();

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline()
                // 15秒客户端没给服务端主动发送消息就触发写空闲，执行MyClientHandler的userEventTriggered方法给服务端发送一次心跳
                .addLast(new IdleStateHandler(0, 15, 0))
                .addLast(new ObjEncoder(NettyMessage.class))
                .addLast(new ObjDecoder(NettyMessage.class))
                //.addLast(new StringDecoder())
               // .addLast(new StringEncoder())
                .addLast(myClientHandler);

    }
}
