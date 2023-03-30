package com.wwm.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    /**
     * 如果使用Springboot默认内置的tomcat容器，则必须注入ServerEndpoint的bean；
     * 如果使用外置的web容器，则不需要提供ServerEndpointExporter，下面的注入可以注解掉
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
