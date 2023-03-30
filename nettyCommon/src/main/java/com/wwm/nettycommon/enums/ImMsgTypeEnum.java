package com.wwm.nettycommon.enums;

public enum ImMsgTypeEnum {

    /**
     * 文本消息
     */
    TEXT(1,"文本消息"),
    RED_PACKET(2,"红包消息"),
    VOICE(3,"语音消息"),
    VIDEO(4,"视频消息"),

    ;

    ImMsgTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    private Integer type;

    private String desc;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
