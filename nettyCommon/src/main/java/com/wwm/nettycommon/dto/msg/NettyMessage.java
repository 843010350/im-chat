package com.wwm.nettycommon.dto.msg;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NettyMessage {

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

    /**
     * 发送消息类型 1、longin 2、
     */
    private Integer sendType;

    private String viewType;

    public NettyMessage(Integer sendUserId, String sendMsg, Integer receiveUserId, Integer sendType) {
        this.sendUserId = sendUserId;
        this.sendMsg = sendMsg;
        this.receiveUserId = receiveUserId;
        this.sendType = sendType;
    }

    public NettyMessage(Integer sendUserId) {
        this.sendUserId = sendUserId;
    }

    //----------------------以上是登录心跳消息-------------------








    /**
     * 如果是群聊则是groupId,私聊则是对方的用户id
     */
    private String id;


    /**
     * 用户名字
     */
    private String username;

    /**
     * 头像签名
     */
    private String avatar;

    /**
     * group 或者 friend
     */
    private String type;



    /**
     * 对方接收消息的内容
     */
    private String content;

    /**
     * 消息id 可不传
     */
    private Integer cid;

    private Boolean mine = false;

    /**
     * 消息的发送者id
     */
    private String fromid;

    /**
     * 发送消息的时间戳
     */
    private Long timestamp;

}
