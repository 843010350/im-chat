package com.wwm.nettyserver.server;

import com.alibaba.fastjson.JSON;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.handler.HeartBeatHandler;
import com.wwm.nettycommon.handler.RouteHandler;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.session.SessionSocketHolder;
import com.wwm.nettycommon.utils.NettyAttrUtil;
import com.wwm.nettycommon.utils.SpringBeanFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ChannelHandler.Sharable
public class MyNettyServerHandler extends SimpleChannelInboundHandler<NettyMessage> {

    private static NettyMessage heartBeat = new NettyMessage();

    static {
        heartBeat.setSendUserId(0);
        heartBeat.setReceiveUserId(0);
        heartBeat.setSendMsg("ping");
        heartBeat.setSendType(Constants.PING);
    }
    /**
     * 自定义编解码7⃣器之后
     * 只有在编解码器处理完成之后才会调用channelRead0方法
     * 这里也可以处理会写客户端的逻辑
     *
     *
     * @param channelHandlerContext
     * @param nettyMessage
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, NettyMessage nettyMessage) throws Exception {
        log.info("服务端接收到的信息:{}", JSON.toJSONString(nettyMessage));

        if (nettyMessage.getSendType() == Constants.LOGIN) {
            //保存客户端与 Channel 之间的关系
            SessionSocketHolder.put(nettyMessage.getSendUserId(), (NioSocketChannel) channelHandlerContext.channel());
            SessionSocketHolder.saveSession(nettyMessage.getSendUserId(), nettyMessage.getSendMsg());//储存用户名字
            log.info("client [{}] online success!!", nettyMessage.getSendMsg());
        }

        //心跳更新时间
        if (nettyMessage.getSendType() == Constants.PING) {
            NettyAttrUtil.updateReaderTime(channelHandlerContext.channel(), System.currentTimeMillis());
            //向客户端响应 pong 消息
            channelHandlerContext.writeAndFlush(heartBeat).addListeners((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    log.error("IO error,close Channel");
                    future.channel().close();
                }
            });
        }
    }





    /**
     * 客户端注册
     * @param ctx
     * @throws Exception
     */
/*    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端注册");
        super.channelRegistered(ctx);
    }*/

/*

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("管道活跃");
        ctx.read();
        super.channelActive(ctx);
    }
*/

    /**
     * 监听用户下线操作
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //可能出现业务判断离线后再次触发 channelInactive
        User userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        if (userInfo != null) {
            log.warn("[{}] trigger channelInactive offline!", userInfo.getUsername());
            //Clear route info and offline.
            RouteHandler routeHandler = SpringBeanFactory.getBean(RouteHandler.class);
            routeHandler.userOffLine(userInfo, (NioSocketChannel) ctx.channel());
            ctx.channel().close();
        }
       // ctx.fireChannelInactive();

    }



    /**
     * 这个方法在解码完成前可能会调用多次，由系统调用
     *
     * 业务端用netty写了一个http服务器，
     * 发现channelReadComplete方法被调用多次问题，
     * 通过对客户端请求消息和Netty框架进行源码分析，找到了问题的根本原因:TCP底层并不了解上层业务数据的具体含义，
     * 它会根据TCР缓冲区的实际情况进行包的拆分，所以在业务上认为一个完整的 HTTP报文可能会被TCP
     * 拆分成多个包发送也有可能把多个小的包封装成一个大的数据包发送。
     *
     * 所以这里需要谨慎处理(并不能来消息就flush)
     *
     * @param ctx
     * @throws Exception
     */
 /*   @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("管道读取完成");
        super.channelReadComplete(ctx);
    }*/

    /**
     * 读空闲操作
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                //TIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
                //System.out.println("定时检测客户端是否存活:" + userInfo.getUserName());
                HeartBeatHandler heartBeatHandler = SpringBeanFactory.getBean(HeartBeatHandler.class);
                heartBeatHandler.process(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * exceptionCaught方法就是发生异常的处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info(cause.getMessage(),cause);  //打印信息
        ctx.close(); //关闭管道
    }










}
