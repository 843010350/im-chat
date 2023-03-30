package com.wwm.nettycommon.handler;

import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.nettyClient.NettyClient;
import com.wwm.nettycommon.service.RouteService;
import com.wwm.nettycommon.service.UserService;
import com.wwm.nettycommon.session.ClientSessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientHearBeatHandler {
    @Autowired
    private RouteService routeService;

    @Autowired
    private UserService userService;
    /**
     *客户端重连逻辑
     * @param ctx
     * @param user
     */
    public void process(ChannelHandlerContext ctx, User user) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        try {
            ClientSessionSocketHolder.getSession(user.getId()).setOffLine(false);
            ClientSessionSocketHolder.getSession(user.getId()).setReconnect(true);
            routeService.userOffLine(user);
            ClientSessionSocketHolder.remove(channel);
            ClientSessionSocketHolder.removeSession(user.getId());
            userService.connectNettyServer(user,user.getId());
        }finally {
            user.setReconnect(false);
            ClientSessionSocketHolder.put(channel,user.getId());
            ClientSessionSocketHolder.saveSession(user.getId(),user);
        }
    }
}
