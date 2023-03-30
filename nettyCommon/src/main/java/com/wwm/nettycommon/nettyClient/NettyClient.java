package com.wwm.nettycommon.nettyClient;

import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.model.ServerInfoDto;
import com.wwm.nettycommon.session.ClientSessionSocketHolder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class NettyClient {

    private SocketChannel channel;
    /**
     * 线程组
     */
    private EventLoopGroup group = new NioEventLoopGroup(1, new DefaultThreadFactory("MyClient"));

    public void initUserConnectServer(ServerInfoDto infoDto, User userInfo){
        int userFailCount = 0;
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new MyClientInit());

        ChannelFuture future = null;
        try {
            future = bootstrap.connect(infoDto.getServerIp(), infoDto.getServerPort()).sync();
            loginNettyServer(future,userInfo);
        } catch (Exception e) {
            userFailCount++;

            if (userFailCount >= userInfo.getUserFailCount()) {
                log.error("用户:{},连接超过最大次数:{}",userInfo.getId(),userInfo.getUserFailCount());
                //客户端下线操作
               // msgHandle.shutdown(); todo
                return;
            }
            log.error("链接失败", e);
        }
        if (future.isSuccess()) {
            log.info("用户:{},启动成功",userInfo.getId());
        }
        channel = (SocketChannel) future.channel();
        ClientSessionSocketHolder.saveSession(userInfo.getId(),userInfo);
        ClientSessionSocketHolder.put(channel,userInfo.getId());
    }

    /**
     * 向服务器注册
     */
    public void loginNettyServer(ChannelFuture future,User userInfo) {
        NettyMessage login = new NettyMessage(userInfo.getId(), userInfo.getUsername(),null, Constants.LOGIN);
        future.channel().writeAndFlush(login);
        /**
         * 异步监听发送消息成功
         */
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("用户:{},发送登录消息成功",userInfo.getId())
        );
    }
}
