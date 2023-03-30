package com.wwm.userservice.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private CheckTokenInterceptor checkTokenInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String>  list = new ArrayList<>();
        list.add("/api/user/userLogin");
        registry.addInterceptor(checkTokenInterceptor)
                .addPathPatterns("/websocket/**")
                .addPathPatterns("/api/**")
                .excludePathPatterns(list);
    }
}
