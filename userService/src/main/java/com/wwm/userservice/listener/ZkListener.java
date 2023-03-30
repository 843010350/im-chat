package com.wwm.userservice.listener;

import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.utils.ZkUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ZkListener {
    @Autowired
    private ZkUtils zkUtils;


    /**
     * 对当前节点下的子节点进行监听
     * @throws Exception
     */
    @PostConstruct
    public void initListener() throws Exception {
        zkUtils.subscribeEvent(Constants.ZK_ROUTE);
    }
}
