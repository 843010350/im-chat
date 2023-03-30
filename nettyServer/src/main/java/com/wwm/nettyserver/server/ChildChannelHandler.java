package com.wwm.nettyserver.server;

import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.protocol.ObjDecoder;
import com.wwm.nettycommon.protocol.ObjEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;


public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    private final MyNettyServerHandler myNettyServerHandler = new MyNettyServerHandler();

    @Override
    protected void initChannel(SocketChannel channel) {
        System.out.println(123);
        // 20秒没有收到客户端发送消息或心跳就触发读空闲，执行MyNettyServerHandler的userEventTriggered方法关闭客户端连接
        channel.pipeline()
                .addLast(new LoggingHandler(LogLevel.INFO))
                .addLast(new IdleStateHandler(20, 0, 0))
                .addLast(new ObjEncoder(NettyMessage.class))
                .addLast(new ObjDecoder(NettyMessage.class))
               //.addLast(new StringDecoder())
               // .addLast(new StringEncoder())
                .addLast(myNettyServerHandler);


    }

}
