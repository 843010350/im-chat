package com.wwm.nettyserver;

import com.wwm.nettycommon.utils.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.net.InetAddress;

@SpringBootApplication(scanBasePackages = {"com.wwm"})
@EnableFeignClients(basePackages = {"com.wwm"})
@Slf4j
public class NettyServerApplication implements CommandLineRunner {
    @Value("${netty.server.port:9999}")
    private Integer nettyServerPort;

    @Value("${server.port:9987}")
    private Integer httpPort;

    @Autowired
   private ZkUtils zkUtils;

    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String addr = InetAddress.getLocalHost().getHostAddress();
        String registry = zkUtils.getRegistryList(addr, nettyServerPort, httpPort);
        // 添加临时序号节点
        zkUtils.createEphemeral(registry);
        log.info("注册zk成功");
    }
}
