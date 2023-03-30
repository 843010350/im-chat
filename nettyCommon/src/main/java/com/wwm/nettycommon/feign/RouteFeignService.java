package com.wwm.nettycommon.feign;

import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.response.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * nettyServer调用用户服务操作
 */
@FeignClient(value = "userService",contextId = "routeFeign")
public interface RouteFeignService {

    @PostMapping("/api/route/userOffLine")
     Result userOffLine(@RequestBody User user);
}
