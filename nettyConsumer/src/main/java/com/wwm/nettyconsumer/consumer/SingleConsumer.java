package com.wwm.nettyconsumer.consumer;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wwm.nettycommon.dto.msg.NettyMessage;
import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.entity.MsgContent;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.entity.UserMsgBox;
import com.wwm.nettycommon.enums.BoxTypeEnum;
import com.wwm.nettycommon.enums.MsgReceiveEnum;
import com.wwm.nettycommon.model.SendMessageDto;
import com.wwm.nettycommon.service.MsgContentService;
import com.wwm.nettycommon.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


/**
 * 单聊消息消费者处理
 */
@Component
public class SingleConsumer {

    @Autowired
    private UserService userService;

    @Autowired
    private MsgContentService msgContentService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "single_topic",groupId = "singleGroup")
    public void singleConsumer(ConsumerRecord<String, String> record, Acknowledgment ack){
        String key = record.key();
        String value = record.value();
        ReceiveMessageDto sendMessageDto = JSON.parseObject(value, ReceiveMessageDto.class);
        //判断是否被消消费过 前端给出唯一标识(防止前端重复发送入库)
        //入库
        userService.saveMessage(sendMessageDto);
        redisTemplate.opsForValue().set("is_exists:"+sendMessageDto.getMsgId(),"1",10,TimeUnit.SECONDS);
        //redis存入消息(离线消息)todo
        ack.acknowledge();
    }

    @KafkaListener(topics = "single_topic_read",groupId = "singleGroup")
    public void singleConsumerRead(ConsumerRecord<String, String> record, Acknowledgment ack){
        String value = record.value();
        NettyMessage sendMessageDto = JSON.parseObject(value, NettyMessage.class);
        String s = redisTemplate.opsForValue().get("is_exists:" + sendMessageDto.getCid());
        if(StringUtils.isEmpty(s)){
            ack.nack(1000L);
        }
        Integer cid = sendMessageDto.getCid();
        UpdateWrapper<MsgContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MsgContent::getIsReceived,MsgReceiveEnum.RECEIVED.getType());
        updateWrapper.lambda().eq(MsgContent::getMid,cid);
        msgContentService.update(updateWrapper);
        ack.acknowledge();
    }
}
