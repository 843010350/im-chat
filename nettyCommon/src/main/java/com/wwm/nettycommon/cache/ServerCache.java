package com.wwm.nettycommon.cache;

import com.google.common.cache.LoadingCache;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.exception.BusinessException;
import com.wwm.nettycommon.response.EnumCode;
import com.wwm.nettycommon.service.UserService;
import com.wwm.nettycommon.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器节点缓存
 *
 * @since JDK 1.8
 */
@Component
@Slf4j
public class ServerCache {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    /**
     * todo 本地缓存，为了防止内存撑爆，后期可换为 LRU。
     */
    private final static Map<Integer, User> USER_INFO_MAP = new ConcurrentHashMap<>(64);


    @Autowired
    private LoadingCache<String, String> cache;


    @Autowired
    private CuratorFramework curatorFramework;


    public void addCache(String key) {
        cache.put(key, key);
    }


    /**
     * 更新所有缓存/先删除 再新增
     *
     * @param currentChildren
     */
    public void updateCache(List<String> currentChildren) {
        cache.invalidateAll();
        for (String currentChild : currentChildren) {
            String key;
            if (currentChild.split("-").length == 2) {
                key = currentChild.split("-")[1];
            } else {
                key = currentChild;
            }
            addCache(key);
        }
    }


    /**
     * 获取所有的服务列表
     *
     * @return
     */
    public List<String> getServerList() throws Exception {

        List<String> list = new ArrayList<>();

        if (cache.size() == 0) {
            List<String> allNode = curatorFramework.getChildren().forPath(Constants.ZK_ROUTE);
            for (String node : allNode) {
                String key = node.split("-")[1];
                addCache(key);
            }
        }
        for (Map.Entry<String, String> entry : cache.asMap().entrySet()) {
            list.add(entry.getKey());
        }
        return list;

    }

    /**
     * rebuild cache list
     */
    public void rebuildCacheList() throws Exception {
        updateCache(getServerList());
    }


    public User loadUserInfoByUserId(Integer userId) {

        //优先从本地缓存获取
        User user = USER_INFO_MAP.get(userId);
        if (user != null) {
            return user;
        }

        //load redis
        String sendUserName = redisUtil.getStr(Constants.ACCOUNT_PREFIX + userId);
        if (sendUserName != null) {
            user= new User();
            user.setId(userId);
            user.setUsername(sendUserName);
            USER_INFO_MAP.put(userId, user);
            return user;
        }
        user = userService.getById(userId);
        if(Objects.isNull(user)){
            log.error("当前用户信息不存在");
            throw  new BusinessException(EnumCode.FAIL.getCode(), "当前发送的用户信息不存在");
        }
        redisUtil.setStr(Constants.ACCOUNT_PREFIX + userId,user.getUsername());
        return user;
    }

}
