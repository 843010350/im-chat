package com.wwm.nettycommon.dto.msg;

import com.wwm.nettycommon.entity.User;
import lombok.Data;

@Data
public class ReceiveMessageDto {
    /**
     * 消息发送类型
     */
    private String type;

    /**
     * 消息发送方
     */
    private MineDto mine;

    /**
     * 消息接收方
     */
    private ToDto to;

    private Integer msgId;



}
