package com.wwm.nettycommon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfoDto {

    /**
     * netty服务端ip
     */
    private String serverIp;

    /**
     * netty服务端端口
     */
    private Integer serverPort;

    private Integer httpPort;

    private Integer userId;

}
