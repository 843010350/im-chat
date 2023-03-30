package com.wwm.nettycommon.handler;


import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.feign.RouteFeignService;
import com.wwm.nettycommon.response.Result;
import com.wwm.nettycommon.session.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * @since JDK 1.8
 */
@Component
@Slf4j
public class RouteHandler {


    @Autowired
    private RouteFeignService routeFeignService;



    /**
     * 用户下线
     */
    public void userOffLine(User user, NioSocketChannel channel) throws IOException {
        if (user != null) {
            log.info("Account [{}] offline", user.getUsername());
            /**
             * 移除session
             */
            SessionSocketHolder.removeSession(user.getId());
            /**
             * 清除用户路由关系
             */
            clearRouteInfo(user);
        }
        /**
         * 移除管道操作
         */
        SessionSocketHolder.remove(channel);
    }


    /**
     * 清除路由关系
     * @throws IOException
     */
    public void clearRouteInfo(User user) {
        Result result = routeFeignService.userOffLine(user);
    }

}
