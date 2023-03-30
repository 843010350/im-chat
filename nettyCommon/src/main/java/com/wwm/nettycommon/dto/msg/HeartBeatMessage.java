package com.wwm.nettycommon.dto.msg;

import lombok.Data;

@Data
public class HeartBeatMessage extends NettyMessage {

    /**
     * 请求唯一标记
     */
    private Integer sendUserId;

    /**
     * 请求消息
     */
    private String sendMsg;

    /**
     * 请求的消息类型
     */
    private Integer receiveUserId;
}
