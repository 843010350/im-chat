package com.wwm.nettycommon.session;

import com.wwm.nettycommon.entity.User;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class SessionSocketHolder {
    private static final Map<Integer, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    private static final Map<Integer, String> SESSION_MAP = new ConcurrentHashMap<>(16);

    public static void saveSession(Integer userId, String userName) {
        SESSION_MAP.put(userId, userName);
    }

    public static void removeSession(Integer userId) {
        SESSION_MAP.remove(userId);
    }

    /**
     * Save the relationship between the userId and the channel.
     *
     * @param id
     * @param socketChannel
     */
    public static void put(Integer id, NioSocketChannel socketChannel) {
        CHANNEL_MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(Integer id) {
        return CHANNEL_MAP.get(id);
    }

    public static Map<Integer, NioSocketChannel> getRelationShip() {
        return CHANNEL_MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }


    /**
     * 获取注册用户信息
     *
     * @param nioSocketChannel
     * @return
     */
    public static User getUserId(NioSocketChannel nioSocketChannel) {
        for (Map.Entry<Integer, NioSocketChannel> entry : CHANNEL_MAP.entrySet()) {
            NioSocketChannel value = entry.getValue();
            if (nioSocketChannel == value) {
                Integer key = entry.getKey();
                String userName = SESSION_MAP.get(key);
                User info = new User();
                info.setId(key);
                info.setUsername(userName);
                return info;
            }
        }

        return null;
    }




}
