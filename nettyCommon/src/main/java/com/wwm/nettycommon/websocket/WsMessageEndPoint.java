package com.wwm.nettycommon.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.dto.msg.MineDto;
import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.dto.msg.ToDto;
import com.wwm.nettycommon.enums.SendMessageType;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.service.RouteService;
import com.wwm.nettycommon.utils.RedisUtil;
import com.wwm.nettycommon.utils.SpringBeanFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.wwm.nettycommon.websocket.WsMessageEndPoint.PATH;


/**
 * websocket断点
 */
@Slf4j
@Service
@ServerEndpoint(PATH)
public class WsMessageEndPoint {
    /**
     * localhost:8080/websocket/message
     * 路径参数用于客户端身份标识，区分每个客户端的连接
     */
    public static final String PATH = "/websocket/message/{token}";

    @Autowired
    private RouteService routeService;

    /**
     * 客户端会话管理容器
     */
    private static final Map<Integer, Session> CLIENT_SESSION_MAP = new ConcurrentHashMap<>();

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 侦测客户端开启连接？，通过客户端身份标识 重复创建连接则覆盖原有的连接
     * @param session
     */
    @OnOpen
    public void onConnectionOpen(Session session, @PathParam("token") String clientToken) {
        log.info("客户端 {} 开启了连接", clientToken);
        final Map<String, Object> map = JSONObject.parseObject(clientToken, Map.class);
        // 默认按照ID保管存放这个客户端的会话信息
        Integer id = Integer.valueOf(map.get("id").toString());
        CLIENT_SESSION_MAP.put(id, session);
        System.out.println(123456);
    }

    /**
     * 侦测客户端关闭事件
     * @param session
     */
    @OnClose
    public void onClose(Session session,  @PathParam("token") String clientToken) {
        final Map<String, String> map = JSONObject.parseObject(clientToken, Map.class);
        log.info("客户端 {} 关闭了连接...", clientToken);
        // 客户端关闭时，从保管容器中踢出会话
        final String userId = map.get("userId");
        CLIENT_SESSION_MAP.remove(userId);
    }

    /**
     * 侦测客户端异常
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("客户端 {} 连接异常... 异常信息：{}", session.getId(), error.getMessage());
    }



    /**
     * 收到客户端消息
     * @param session
     */
    @OnMessage
    public void receiveClientMessage(String message, Session session) throws IOException {
        log.info("来自客户端 {} 的消息: {}", session.getId(), message);
        ReceiveMessageDto receiveMessageDto = JSON.parseObject(message, ReceiveMessageDto.class);
        String type = receiveMessageDto.getType();

        if(SendMessageType.CONNECTION.getDesc().equals(type)){
            log.info("来自客户端 {} 的连接成工消息: {}", session.getId(), message);
            return;
        }
        type = receiveMessageDto.getTo().getType();
        receiveMessageDto.setType(type);
        if(Objects.isNull(routeService)){
            routeService = SpringBeanFactory.getBean(RouteService.class);
        }
        if(SendMessageType.FRIEND.getDesc().equals(type)){
            //单聊消息
            routeService.sendSingleMessage(receiveMessageDto);
            return;
        }

        if(SendMessageType.GROUP.getDesc().equals(type)){
            //群聊消息
        }

        session.getBasicRemote().sendText("服务器已收到");
    }

    /**
     * 给所有客户端发送消息
     * @param message
     */
    public static void sendMessageForAllClient(String message) {
        CLIENT_SESSION_MAP.values().forEach(session -> {
            try {
                log.info("给客户端 {} 发送消息 消息内容: {}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
                log.info("给客户端 {} 发送消息失败， 异常信息：{}", session.getId(), e.getMessage());
            }
        });
    }

    /**
     * 给指定客户端发送消息
     *
     * @param clientId
     */
    @SneakyThrows
    public static void sendMessageForClient(String clientId,String message) {
        final Session session = CLIENT_SESSION_MAP.get(Integer.valueOf(clientId));
        session.getBasicRemote().sendText(message);
    }
}