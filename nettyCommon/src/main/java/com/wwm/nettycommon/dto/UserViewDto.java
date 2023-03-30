package com.wwm.nettycommon.dto;

import com.wwm.nettycommon.entity.User;
import lombok.Data;

@Data
public class UserViewDto extends User {
    /**
     * 用户zuid
     */
    private Integer groupId;



}
