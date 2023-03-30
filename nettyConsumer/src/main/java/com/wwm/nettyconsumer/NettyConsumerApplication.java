package com.wwm.nettyconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.wwm"})
@EnableFeignClients(basePackages = {"com.wwm"})
public class NettyConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyConsumerApplication.class, args);
    }

}
