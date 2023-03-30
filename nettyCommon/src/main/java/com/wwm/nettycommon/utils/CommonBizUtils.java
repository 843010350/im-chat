package com.wwm.nettycommon.utils;


import com.wwm.nettycommon.cache.ServerCache;
import com.wwm.nettycommon.exception.BusinessException;
import com.wwm.nettycommon.model.ServerInfoDto;
import com.wwm.nettycommon.response.EnumCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @since JDK 1.8
 */
@Component
@Slf4j
public class CommonBizUtils {


    @Autowired
    private ServerCache serverCache;

    /**
     * check ip and port
     */
    public void checkServerAvailable(ServerInfoDto serverInfoDto) throws Exception {
        boolean reachable = NetAddressIsReachable.checkAddressReachable(serverInfoDto.getServerIp(), serverInfoDto.getServerPort(), 1000);
        if (!reachable) {
            log.error("ip={}, port={} are not available", serverInfoDto.getServerIp(), serverInfoDto.getServerPort());

            // rebuild cache
            serverCache.rebuildCacheList();
            EnumCode un_avAiLABLE = EnumCode.UN_AVAiLABLE;
            throw new BusinessException(un_avAiLABLE.getCode(),un_avAiLABLE.getDesc());
        }

    }
}
