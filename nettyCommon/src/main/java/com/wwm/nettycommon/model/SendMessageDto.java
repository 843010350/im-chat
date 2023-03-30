package com.wwm.nettycommon.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendMessageDto {


    @NotNull(message = "userId 不能为空")
    //消息发送者的 userId
    private Integer userId;


    @NotNull(message = "userId 不能为空")
    //消息接收者的 userId
    private Integer receiveUserId;


    @NotNull(message = "msg 不能为空")
    private String msg;

    @NotNull(message = "消息类型不能为空")
    private Integer msgType;

    @NotNull(message = "前端请求的唯一标识id不能为空")
    private Integer uniqueRequestId;
}
