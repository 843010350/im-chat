package com.wwm.nettycommon.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import okhttp3.OkHttpClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CuratorConfig {
    @Autowired
    WrapperZK wrapperZk;

    /**
     * 这里的start就是创建完对象放到容器后，需要调用他的start方法
     *
     * @return
     */
    @Bean(initMethod = "start")
    public CuratorFramework curatorFramework() {
        return CuratorFrameworkFactory.newClient(
                wrapperZk.getConnectString(),
                wrapperZk.getSessionTimeoutMs(),
                wrapperZk.getConnectionTimeoutMs(),
                new RetryNTimes(wrapperZk.getRetryCount(), wrapperZk.getElapsedTimeMs()));
    }

    @Bean
    public OkHttpClient feignHttpClient(){
        return new OkHttpClient();
    }

    @Bean
    public LoadingCache<String, String> buildCache() {
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String s) throws Exception {
                        return null;
                    }
                });
    }
}
