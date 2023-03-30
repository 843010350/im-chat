package com.wwm.nettycommon.dto;

import lombok.Data;

@Data
public class SearchUserDto {

    private Integer id;

    private String username;//用户名或者组名

    /**
     * 头像图标
     */
    private String avatar;

    /**
     * 在线签名
     */
    private String sign;
}
