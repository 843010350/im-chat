package com.wwm.nettycommon.dto;

import com.wwm.nettycommon.entity.FriendGroup;
import com.wwm.nettycommon.entity.Group;
import com.wwm.nettycommon.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    private User mine;

    private List<FriendGroup> friend;

    private List<Group> group;

    /**
     * 群组成员列表
     */
    private List<User> list;

}
