package com.wwm.nettycommon.response;

public enum EnumCode {

    SUCCESS(0, "success"),

    FAIL(1, "请求失败"),

    TIME_OUT(408, "请求超时"),
    UN_AVAiLABLE(10010, "zk不可用"),
    NO_LOGIN(10020,"用户未登录"),
    LOGIN_EXPIRED(10030,"用户未登录过期"),
    INVALID_TOKEN(10040,"非法token"),

    REQUEST_LIMIT(503, "请求太多");

    private Integer code;
    private String desc;

    EnumCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
