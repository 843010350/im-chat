package com.wwm.nettycommon.enums;

public enum BoxTypeEnum {


    /**
     * 文本消息
     */
    SEND(1,"发件箱"),
    RECEIVE(2,"收件箱")
    ;

    BoxTypeEnum(Integer type, String desc) {
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
