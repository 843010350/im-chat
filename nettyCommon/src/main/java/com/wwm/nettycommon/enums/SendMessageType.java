package com.wwm.nettycommon.enums;

public enum SendMessageType {
    /**
     * 文本消息
     */
    FRIEND(3,"friend"),
    GROUP(4,"group"),
    CONNECTION(20,"connection")
    ;

    SendMessageType(Integer type, String desc) {
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
