package com.wwm.nettyserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Component
@Slf4j

public class MyNettyServer {
    /**
     * 主线成组
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();

    @Value("${netty.server.port:9999}")
    private Integer nettyServerPort;

    /**
     * 从线程组
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    @PostConstruct
    public void initNettyServer() throws InterruptedException {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(nettyServerPort))
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //保持自动读取

                    .childHandler(new ChildChannelHandler()).childOption(ChannelOption.AUTO_READ,true);


            // 绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                log.info("my client started!");
            }
            // 等待服务端监听端口关闭
            //future.channel().closeFuture().sync(); todo 夯住等待
        }finally {
            // 优雅退出，释放线程池资源
           // bossGroup.shutdownGracefully();
           // workerGroup.shutdownGracefully();
        }

    }
}
