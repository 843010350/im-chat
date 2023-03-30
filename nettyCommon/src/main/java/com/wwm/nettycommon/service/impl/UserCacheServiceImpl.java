package com.wwm.nettycommon.service.impl;

import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCacheServiceImpl {
    @Autowired
    private RedisUtil redisUtil;

    public boolean saveAndCheckUserLoginStatus(Integer userId) throws Exception {
        Long add = redisUtil.add(Constants.LOGIN_STATUS_PREFIX,userId);
        if (add == 0) {
            return false;
        } else {
            return true;
        }
    }
}
