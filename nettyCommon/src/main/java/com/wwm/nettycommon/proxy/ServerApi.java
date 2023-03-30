package com.wwm.nettycommon.proxy;


import com.wwm.nettycommon.dto.msg.ReceiveMessageDto;
import com.wwm.nettycommon.model.SendMessageDto;


public interface ServerApi {


    Object sendMsg(ReceiveMessageDto dto) throws Exception;
}
