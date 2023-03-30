package com.wwm.nettycommon.service;

import com.wwm.nettycommon.dto.SearchUserDto;
import com.wwm.nettycommon.dto.UserDto;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wwm.nettycommon.model.SendMessageDto;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author 
 * @since 2023-03-11
 */
public interface UserService extends IService<User> {

    User login(User user) throws Exception;

    void connectNettyServer(User user,Integer uid) throws Exception;

    void sendMessage(ReceiveMessageDto dto );

    void saveMessage(ReceiveMessageDto sendMessageDto);

    UserDto initUserInfo(Integer uid);

    UserDto getGroupMemberList(Integer groupId);

    void updateMessageRead(NettyMessage nettyMessage);







}
