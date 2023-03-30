package com.wwm.nettycommon.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwm.nettycommon.dto.SearchUserDto;
import com.wwm.nettycommon.dto.UserDto;
import com.wwm.nettycommon.dto.UserViewDto;
import com.wwm.nettycommon.dto.msg.MineDto;
import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.dto.msg.ToDto;
import com.wwm.nettycommon.entity.*;
import com.wwm.nettycommon.enums.BoxTypeEnum;
import com.wwm.nettycommon.enums.MsgReceiveEnum;
import com.wwm.nettycommon.enums.SendMessageType;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.mapper.GroupMapper;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.nettyClient.NettyClient;
import com.wwm.nettycommon.service.*;
import com.wwm.nettycommon.session.SessionSocketHolder;
import com.wwm.nettycommon.utils.*;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.exception.BusinessException;
import com.wwm.nettycommon.mapper.UserMapper;
import com.wwm.nettycommon.model.ServerInfoDto;
import com.wwm.nettycommon.response.EnumCode;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author 
 * @since 2023-03-11
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {



    @Autowired
    private NettyClient nettyClient;

    @Autowired
    private CommonBizUtils commonBizUtils;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ZkUtils zkUtils;

    @Autowired
    private MsgContentService msgContentService;

    @Autowired
    private UserMsgBoxService userMsgBoxService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;



    @Autowired
    private FriendGroupService friendGroupService;



    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupUserService groupUserService;

    @Autowired
    private RedisUtil redisUtil;



    @Override
    public User login(User user) throws Exception {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUsername,user.getUsername());
        User one = this.getOne(queryWrapper);
        if(Objects.isNull(one)){
            throw new BusinessException(EnumCode.FAIL.getCode(),"用户不存在");
        }
        //这里就不校验加密密码  直接铭文校验
        if(!StringUtils.equals(one.getPassword(),user.getPassword())){
            throw new BusinessException(EnumCode.FAIL.getCode(),"密码错误");
        }
        Integer uid = one.getId();
        //简单代替token
       /* boolean b = userCacheService.saveAndCheckUserLoginStatus(uid);
        if(!b){
            return;
        }*/
        connectNettyServer(user,uid);
        String token = JwtUtils.createToken(user);
        User user1 = new User();
        user1.setId(uid);
        user1.setUsername(user.getUsername());
        user1.setToken(token);
        return user1;
    }

    @Override
    public void connectNettyServer(User user,Integer uid) throws Exception {
        user.setId(uid);
        //获取服务端列表
        List<String> list = zkUtils.getChildren(Constants.ZK_ROUTE);
        String server = zkUtils.getRibbon(uid, list);
        ServerInfoDto infoDto = zkUtils.parse(server);
        nettyClient.initUserConnectServer(infoDto,user);
        //nettyClient.loginNettyServer(user);
        //校验zk服务是否存活
        commonBizUtils.checkServerAvailable(infoDto);
        //存储当前用户和服务端的关联关系
        routeService.savaUserRouteInfo(uid,server);
    }

    @Override
    public void sendMessage(ReceiveMessageDto dto) {
        //拿到reciveId
        MineDto mine = dto.getMine();
        ToDto to = dto.getTo();
        String type = to.getType();
        Integer receiveUserId = to.getId();
        //通过reciveId拿到与服务端建立的channel
        NioSocketChannel socketChannel = SessionSocketHolder.get(receiveUserId);
        if(Objects.isNull(socketChannel)){
            //管道为nullze不进行发送
            return;
        }
        //发送给用户  异步监听
        NettyMessage nettyMessage = new NettyMessage();
        BeanUtils.copyProperties(mine,nettyMessage);
        nettyMessage.setId(String.valueOf(mine.getId()));
        nettyMessage.setMine(false);
        nettyMessage.setFromid(String.valueOf(mine.getId()));
        nettyMessage.setContent(mine.getContent());
        nettyMessage.setCid(dto.getMsgId());
        nettyMessage.setType(type);
        if(SendMessageType.GROUP.getDesc().equals(nettyMessage.getType())){
            nettyMessage.setSendType(SendMessageType.GROUP.getType());
        }else {

            nettyMessage.setSendType(SendMessageType.FRIEND.getType());
        }
        nettyMessage.setTimestamp(System.currentTimeMillis());
        nettyMessage.setSendUserId(mine.getId());
        nettyMessage.setReceiveUserId(to.getId());
        nettyMessage.setSendMsg(mine.getContent());
        ChannelFuture future = socketChannel.writeAndFlush(nettyMessage);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("server push msg:[{}]", dto.toString()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(ReceiveMessageDto sendMessageDto) {
        /**
         * 去重id有前端传入作为消息id进行去重
         * 对于消息是否需要排序的问题，这里只提出一个比较通用的方案：建议会话中不强制排序，会话历史记录中按照向量时钟的偏序关系进行排序。
         */
        MsgContent byId = msgContentService.getById(sendMessageDto.getMsgId());
        if(Objects.nonNull(byId)){
            log.warn("重复消费:{}",sendMessageDto.getMsgId());
            return;
        }
        //组装入库类
       // Long snowFlakeId = SnowFlakeUtil.getSnowFlakeId();
        ToDto to = sendMessageDto.getTo();
        String type = to.getType();
        MineDto mine = sendMessageDto.getMine();
        MsgContent msgContent = new MsgContent();
        msgContent.setMid(sendMessageDto.getMsgId());
        msgContent.setContent(mine.getContent());
        msgContent.setSenderId(mine.getId());
        msgContent.setRecipientId(to.getId());
        if(SendMessageType.FRIEND.getDesc().equals(type)){
            msgContent.setMsgType(SendMessageType.FRIEND.getType());
        }else {
            msgContent.setMsgType(SendMessageType.GROUP.getType());
        }

        msgContent.setIsReceived(MsgReceiveEnum.NO_RECEIVE.getType());
        msgContent.setCreateTime(LocalDateTime.now());
        //组装信箱表
        UserMsgBox send = new UserMsgBox();
        send.setOwnerUid(mine.getId());
        send.setOtherUid(to.getId());
        send.setBoxType(BoxTypeEnum.SEND.getType());
        send.setCreateTime(LocalDateTime.now());

        UserMsgBox receive = new UserMsgBox();
        receive.setOwnerUid(to.getId());
        receive.setOtherUid(mine.getId());
        receive.setBoxType(BoxTypeEnum.RECEIVE.getType());
        receive.setCreateTime(LocalDateTime.now());
        //入库
        msgContentService.save(msgContent);
        Integer mid = msgContent.getMid();
        send.setMid(mid);
        receive.setMid(mid);
        userMsgBoxService.save(send);

        userMsgBoxService.save(receive);

    }

    @Override
    public UserDto initUserInfo(Integer uid) {
        User mine = this.getById(uid);
        UserDto dto = new UserDto();
        dto.setMine(mine);
        QueryWrapper<FriendGroup> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FriendGroup::getUserId,uid);
        List<FriendGroup> groupList = friendGroupService.list(queryWrapper);
        if(CollectionUtils.isEmpty(groupList)){
            return dto;
        }
        List<UserViewDto> userFriends = this.getBaseMapper().queryFriends(uid);
        if(CollectionUtils.isEmpty(userFriends)){
            dto.setFriend(groupList);
            return dto;
        }
        Map<Integer, List<UserViewDto>> collect = userFriends.stream().collect(Collectors.groupingBy(UserViewDto::getGroupId));
        groupList.forEach(item->{
            Integer id = item.getId();
            item.setList(collect.get(id));
        });
        dto.setFriend(groupList);
        QueryWrapper<GroupUser> groupUserQueryWrapper = new QueryWrapper<>();
        groupUserQueryWrapper.lambda().eq(GroupUser::getUserId,uid);
        List<GroupUser> list1 = groupUserService.list(groupUserQueryWrapper);
        if(CollectionUtils.isEmpty(list1)){
            return dto;
        }
        List<Integer> collect1 = list1.stream().map(GroupUser::getGroupId).collect(Collectors.toList());
        QueryWrapper<Group> groupQueryWrapper = new QueryWrapper<>();
        groupQueryWrapper.lambda().in(Group::getId,collect1);
        List<Group> list = groupService.list(groupQueryWrapper);
        dto.setGroup(list);
        return dto;
    }

    @Override
    public UserDto getGroupMemberList(Integer groupId) {
        UserDto userDto = new UserDto();
        QueryWrapper<GroupUser> groupUserQueryWrapper = new QueryWrapper<>();
        groupUserQueryWrapper.lambda().eq(GroupUser::getGroupId,groupId);
        List<GroupUser> list = groupUserService.list(groupUserQueryWrapper);
        if(CollectionUtils.isEmpty(list)){
            return userDto;
        }
        List<Integer> collect = list.stream().map(GroupUser::getUserId).collect(Collectors.toList());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().in(User::getId,collect);
        List<User> list1 = this.list(queryWrapper);
        userDto.setList(list1);
        return userDto;
    }

    @Override
    public void updateMessageRead(NettyMessage nettyMessage) {
        kafkaTemplate.send(Constants.SINGLE_TOPIC_READ, 0, "key", JSON.toJSONString(nettyMessage));
    }


}
