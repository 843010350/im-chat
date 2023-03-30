package com.wwm.nettycommon.session;

import com.wwm.nettycommon.entity.User;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientSessionSocketHolder {
    private static final Map<SocketChannel, Integer> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    private static final Map<Integer, User> SESSION_MAP = new ConcurrentHashMap<>(16);

    public static void saveSession(Integer userId, User user) {
        SESSION_MAP.put(userId, user);
    }

    public static User getSession(Integer userId) {
        return SESSION_MAP.get(userId);
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
    public static void put(SocketChannel socketChannel,Integer id) {
        CHANNEL_MAP.put(socketChannel,id);
    }

    public static Integer get(SocketChannel socketChannel) {
        return CHANNEL_MAP.get(socketChannel);
    }


    public static void remove(SocketChannel nioSocketChannel) {
        CHANNEL_MAP.remove(nioSocketChannel);
    }

}
