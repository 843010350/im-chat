package com.wwm.nettycommon.dto.msg;

import lombok.Data;

@Data
public class ToDto {


    /**
     * 用户id
     */

    private Integer id;

    /**
     * 用户名
     */
    private String username;




    /**
     * 头像图标
     */
    private String avatar;

    /**
     * 在线状态
     */
    private String status;

    /**
     * 在线签名
     */
    private String sign;

    /**
     * 发送类型
     */
    private String type;

    private String groupname;


}
