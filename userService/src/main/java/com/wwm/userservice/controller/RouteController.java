package com.wwm.userservice.controller;

import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.response.Result;
import com.wwm.nettycommon.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;


    /**
     * 发送单聊消息
     * @param dto
     * @return
     */
    @PostMapping("/sendSingleMessage")
    public Result sendSingleMessage(@Valid ReceiveMessageDto dto){
        routeService.sendSingleMessage(dto);
        return Result.success();
    }

    /**
     * 用户下线操作
     * @param user
     * @return
     */
    @PostMapping("/userOffLine")
    public Result userOffLine(@RequestBody User user){
        routeService.userOffLine(user);
        return Result.success();
    }
}
