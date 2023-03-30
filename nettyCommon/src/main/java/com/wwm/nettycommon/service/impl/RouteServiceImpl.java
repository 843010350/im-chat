package com.wwm.nettycommon.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.wwm.nettycommon.cache.ServerCache;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.dto.msg.MineDto;
import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.entity.GroupUser;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.feign.RouteFeignService;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.model.ServerInfoDto;
import com.wwm.nettycommon.proxy.ProxyManager;
import com.wwm.nettycommon.proxy.ServerApi;
import com.wwm.nettycommon.service.GroupUserService;
import com.wwm.nettycommon.service.RouteService;
import com.wwm.nettycommon.utils.RedisUtil;
import com.wwm.nettycommon.utils.ThreadPoolUtil;
import com.wwm.nettycommon.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("routeService")
@Slf4j
public class RouteServiceImpl implements RouteService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ZkUtils zkUtils;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ServerCache serverCache;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private RouteFeignService routeFeignService;

    private LoadingCache<Integer, List<String>> groupServerCache;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GroupUserService groupUserService;


    @PostConstruct
    public void init(){
        this.groupServerCache = CacheBuilder.newBuilder()
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .refreshAfterWrite(280, TimeUnit.SECONDS)
                .build(CacheLoader.asyncReloading(new CacheLoader<Integer, List<String>>() {
                    @Override
                    public List<String> load(Integer key)
                            throws Exception {
                        return getGroupServerCache(key);
                    }

                    @Override
                    public ListenableFuture<List<String>> reload(Integer key, List<String> oldValue) throws Exception {
                        return Futures.immediateFuture(getGroupServerCache(key));
                    }
                }, ThreadPoolUtil.threadPool));

    }

    public List<String> getGroupServerCache(Integer key){
        //获取所有的群组员信息
        Set<String> members = redisTemplate.opsForSet().members(Constants.GROUP_USER_ID + key);
        if(CollectionUtils.isNotEmpty(members)){
            return new ArrayList<>(members);
        }
        QueryWrapper<GroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(GroupUser::getGroupId,key);
        List<GroupUser> list = groupUserService.list(queryWrapper);

        List<String> collect = list.stream().map(item-> Constants.ROUTE_PREFIX + item.getUserId()).collect(Collectors.toList());
        String[] arr = collect.toArray(new String[collect.size()]);
        redisTemplate.opsForSet().add("Constants.GROUP_USER_ID" + key, arr);
        return collect;
    }

    @Override
    public void savaUserRouteInfo(Integer userId, String server) {
        String Key = Constants.ROUTE_PREFIX + userId;
        redisUtil.setStr(Key,server);

    }

    @Override
    public String getRouteInfoByUser(Integer userId) {
        String receivedServerInfo = redisUtil.getStr(Constants.ROUTE_PREFIX + userId);
        return receivedServerInfo;
    }

    /**
     * 发送单聊消息
     * @param dto
     */
    @Override
    public void sendSingleMessage(ReceiveMessageDto dto) {
        Integer toId = dto.getTo().getId();
        MineDto mine = dto.getMine();
        String receivedServerInfo = getRouteInfoByUser(toId);
        //拿到接收方的对应的netty缓存服信息务器
        ServerInfoDto serverInfoDto = zkUtils.parse(receivedServerInfo);
        //发送消息到netty服务端 调用netty接口
        String url = "http://" + serverInfoDto.getServerIp() + ":" + serverInfoDto.getHttpPort() +"/api/sendMessage/single";
     //   ServerApi serverApi = new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance();
        //User user = serverCache.loadUserInfoByUserId(dto.getUserId());
        //dto.setMsg(user.getUsername()+":"+ dto.getMsg());
        Response response = null;
        Integer cid = Math.toIntExact(redisUtil.getId());
        dto.setMsgId(cid);
        try {
            response  =(Response) new ProxyManager<>(ServerApi.class, url, okHttpClient).getInstance().sendMsg(dto);
            //response = (Response) serverApi.sendMsg(dto);
        } catch (Exception e) {
            log.error("调用nettyServer接口失败", e);
        } finally {
            response.body().close();
        }
        //入库消息,离线消息
        kafkaTemplate.send(Constants.SINGLE_TOPIC, 0, "key", JSON.toJSONString(dto));
    }

    /**
     * 用户下线操作
     * @param user
     */
    @Override
    public void userOffLine(User user) {
        /**
         * 删除登录信息
         */
        redisUtil.remove(Constants.LOGIN_STATUS_PREFIX,user.getId());

        /**
         * 删除路由信息
         */
        redisUtil.deleteStr(Constants.ROUTE_PREFIX,user.getId());

    }

    @Override
    public void sendGroupMessage(ReceiveMessageDto dto) throws ExecutionException {
        Integer groupId = dto.getTo().getId();
        MineDto mine = dto.getMine();
        //先通过groupId从本地缓存中拿
        List<String> list = groupServerCache.get(groupId);
        //获取netty服务端的信息
        List<String> serverList = redisTemplate.opsForValue().multiGet(list);
        Map<String,List<ServerInfoDto>> map =new HashMap<>();
        Map<String,Integer> groupUserMap = new HashMap<>();
        serverList.forEach(item->{
            ServerInfoDto parse = zkUtils.parse(item);
            List<ServerInfoDto> serverInfoDtos = map.get(item);
            if(CollectionUtils.isEmpty(serverInfoDtos)){
                serverInfoDtos = new ArrayList<>();
                serverInfoDtos.add(parse);
            }
            serverInfoDtos.add(parse);
            map.put(item,serverInfoDtos);
        });
        //批量发送
        //通过groupId没有拿到则直接去调用各个用户的缓存nettyserver信息去拿取

        //发送kafka进行持久化存储和reids离线存储

    }

   /* public static void main(String[] args) throws ExecutionException {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        LoadingCache<Integer, Integer> graphs = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(
                        new CacheLoader<Integer, Integer>() {
                            public Integer load(Integer key)  {
                                return list.get(key);
                            }
                        }
                );
        Integer integer = graphs.get(0);
        System.out.println(integer);

    }*/
}
