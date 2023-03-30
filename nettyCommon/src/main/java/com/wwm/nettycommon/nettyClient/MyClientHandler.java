package com.wwm.nettycommon.nettyClient;

import com.alibaba.fastjson.JSON;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.dto.msg.HeartBeatMessage;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.handler.ClientHearBeatHandler;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.session.ClientSessionSocketHolder;
import com.wwm.nettycommon.utils.NettyAttrUtil;
import com.wwm.nettycommon.utils.SpringBeanFactory;
import com.wwm.nettycommon.websocket.WsMessageEndPoint;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static NettyMessage heartBeat = new NettyMessage();
    static {
        heartBeat.setSendUserId(0);
        heartBeat.setReceiveUserId(0);
        heartBeat.setSendMsg("ping");
        heartBeat.setSendType(Constants.PING);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage msg) throws Exception {
    log.info("我是客户端");
        //心跳更新时间
        Integer sendType = msg.getSendType();
        String sendMsg = msg.getContent();
        if (sendType == Constants.PING) {
            //LOGGER.info("收到服务端心跳！！！");
            NettyAttrUtil.updateReaderTime(ctx.channel(), System.currentTimeMillis());
            return;
        }
        /**
         * 单聊消息
         */
        if(sendType == Constants.SINGLE_MSG){
            //websocket回写
            msg.setViewType("chatMessage");
            WsMessageEndPoint.sendMessageForClient(String.valueOf(msg.getReceiveUserId()), JSON.toJSONString(msg));
            return;
        }
        /**
         * 群聊消息
         */
        if(sendType == Constants.GROUP_MSG){
            //群聊消息
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                //System.out.println("客户端给服务端发送心跳");
                ctx.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        //发送失败则关闭通道
                        log.error("IO error,close Channel");
                        future.channel().close();
                    }
                });
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        //客户端和服务端建立连接时调用
        log.info("server connect success!");
    }


    /**
     * 下线操作
     * 判断用户是否是主动下线
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //判断用户是否主动下线 从系统中获取当前用户信息，这里直接写成固定的了
        Channel channel = ctx.channel();
        if (channel != null && channel.isActive()) {
            return;
        }
        SocketChannel socketChannel = (SocketChannel) ctx.channel();
        Integer uid = ClientSessionSocketHolder.get(socketChannel);
        User session = ClientSessionSocketHolder.getSession(uid);
        if (session.isOffLine() || session.isReconnect()) { //主动下线或者主动已经重连
            //主动下线的直接返回
            return;
        }
        log.info("客户端断开连接了");
        //重新连接
        ClientHearBeatHandler bean = SpringBeanFactory.getBean(ClientHearBeatHandler.class);
        bean.process(ctx, session);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //异常时断开连接
        cause.printStackTrace();
        ctx.close();
    }
}
