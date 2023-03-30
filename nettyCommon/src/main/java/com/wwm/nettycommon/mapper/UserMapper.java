package com.wwm.nettycommon.mapper;

import com.wwm.nettycommon.dto.SearchUserDto;
import com.wwm.nettycommon.dto.UserViewDto;
import com.wwm.nettycommon.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2023-03-11
 */
public interface UserMapper extends BaseMapper<User> {

    List<UserViewDto> queryFriends(Integer userId);


}
