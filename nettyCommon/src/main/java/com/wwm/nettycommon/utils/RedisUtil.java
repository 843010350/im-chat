package com.wwm.nettycommon.utils;

import com.wwm.nettycommon.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置String 字符串  默认设置5天的时间
     * @param key
     * @param value
     */
    public void setStr(String key, String value){
        redisTemplate.opsForValue().set(key,value,5L, TimeUnit.DAYS);
    }

    public String getStr(String key){
        String s = redisTemplate.opsForValue().get(key);
        return s;
    }

    public boolean deleteStr(String prefix,Integer userId){
        Boolean delete = redisTemplate.delete(prefix + userId);
        return delete;
    }

    /**
     * set添加
     * @param userId
     * @return
     */
    public Long add(String prefix,Integer userId){
        Long add = redisTemplate.opsForSet().add(prefix, userId.toString());
        return add;
    }

    /**
     * set移除
     * @param userId
     * @return
     */
    public Long remove(String prefix,Integer userId){
        Long remove = redisTemplate.opsForSet().remove(prefix, userId.toString());
        return remove;
    }

    public Long getId(){
        Long id = redisTemplate.opsForValue().increment("idGen");
        return id;
    }


}
