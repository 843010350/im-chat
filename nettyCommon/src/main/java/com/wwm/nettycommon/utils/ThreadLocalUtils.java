package com.wwm.nettycommon.utils;

import com.wwm.nettycommon.entity.User;

public class ThreadLocalUtils {
    public static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public void setThreadUser(User user){
        userThreadLocal.set(user);
    }

    public User getThreadUser(){
       return userThreadLocal.get();
    }
    public void removeThreadUser(){
        userThreadLocal.remove();
    }
}
