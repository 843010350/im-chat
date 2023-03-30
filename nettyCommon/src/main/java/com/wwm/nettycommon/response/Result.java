package com.wwm.nettycommon.response;


import lombok.Data;

@Data
public class Result<T> {
    private Boolean success;
    private Integer code;
    private String msg;
    private T data;


    public static Result success() {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(0);
        result.setMsg("success");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(0);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(message);
        return result;
    }

    public static <T> Result<T> fail(EnumCode enumCode) {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(enumCode.getCode());
        result.setMsg(enumCode.getDesc());
        return result;
    }
}
