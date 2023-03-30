package com.wwm.nettyserver.controller;

import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.response.Result;
import com.wwm.nettycommon.service.UserService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sendMessage")
public class SendMessageController {

    /**
     * netty服务端发送单聊消息
     */
    @Autowired
    private UserService userService;
    @PostMapping("/single")
    public Result sendMessage(@RequestBody ReceiveMessageDto dto){
        userService.sendMessage(dto);
        return Result.success();
    }
}
