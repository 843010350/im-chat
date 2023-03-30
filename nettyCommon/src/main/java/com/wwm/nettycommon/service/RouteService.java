package com.wwm.nettycommon.service;

import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.model.SendMessageDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.ExecutionException;

public interface RouteService {

    void savaUserRouteInfo(Integer userId,String server);

    String getRouteInfoByUser(Integer userId);

    void sendSingleMessage(ReceiveMessageDto dto);

    void userOffLine(User user);

    void sendGroupMessage(ReceiveMessageDto dto) throws ExecutionException;
}
