package com.wwm.nettycommon.enums;

public enum MsgReceiveEnum {

    /**
     * 文本消息
     */
    NO_RECEIVE(0,"未接收"),
    RECEIVED(1,"已接收")
    ;

    MsgReceiveEnum(Integer type, String desc) {
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
