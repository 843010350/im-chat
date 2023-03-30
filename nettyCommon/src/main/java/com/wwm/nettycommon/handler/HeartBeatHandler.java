package com.wwm.nettycommon.handler;

import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.session.SessionSocketHolder;
import com.wwm.nettycommon.utils.NettyAttrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 * @since JDK 1.8
 */
@Component
@Slf4j
public class HeartBeatHandler {

    @Value("${netty.heartbeat.time:30}")
    private long heartBeatTime;

    @Autowired
    private RouteHandler routeHandler;


    /**
     * 处理心跳
     *
     * @param ctx
     * @throws Exception
     */
    public void process(ChannelHandlerContext ctx) throws IOException {
        long heartBeatTime1 = heartBeatTime * 1000;

        Long lastReadTime = NettyAttrUtil.getReaderTime(ctx.channel());
        long now = System.currentTimeMillis();
        if (lastReadTime != null && now - lastReadTime > heartBeatTime1) {
            User userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
            if (userInfo != null) {
                log.warn("客户端[{}]心跳超时[{}]ms，需要关闭连接!", userInfo.getUsername(), now - lastReadTime);
            }
            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());
            ctx.channel().close();
        }
    }
}
