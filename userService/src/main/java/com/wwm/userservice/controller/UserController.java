package com.wwm.userservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwm.nettycommon.dto.SearchUserDto;
import com.wwm.nettycommon.dto.UserDto;
import com.wwm.nettycommon.dto.UserViewDto;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.response.Result;
import com.wwm.nettycommon.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;



    /**
     * 登录逻辑校验
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/userLogin")
    public Result userLogin(@Valid @RequestBody User user) throws Exception {
        User user1 = userService.login(user);
        return Result.success(user1);
    }

    /**
     * 初始化信息
     * @param id
     * @param username
     * @return
     */
    @PostMapping("/getUserInfoList")
    public Result getUserInfoList(@RequestParam Integer id,@RequestParam String username){
        UserDto dto = userService.initUserInfo(id);
        return Result.success(dto);
    }

    /**
     * 查看取成员
     * @param id
     * @return
     */
    @GetMapping(value="/getMembers")
    public Result getGroupMemberList(@RequestParam Integer id){
        UserDto groupMemberList = userService.getGroupMemberList(id);
        return Result.success(groupMemberList);
    }

    @PostMapping("updateMsgRead")
    public Result updateMsgRead(@RequestBody NettyMessage nettyMessage){
    userService.updateMessageRead(nettyMessage);
    return Result.success();
    }






}
