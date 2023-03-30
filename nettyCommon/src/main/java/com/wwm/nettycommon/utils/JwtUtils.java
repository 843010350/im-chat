package com.wwm.nettycommon.utils;

import com.wwm.nettycommon.entity.User;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    public static String createToken(User user){
        Map<String,Object> map = new HashMap<>();
        map.put("id",user.getId());
        map.put("username",user.getUsername());
        JwtBuilder builder = Jwts.builder();
        String token = builder.setSubject(user.getUsername())                     //载荷部分，主题，就是token中携带的数据，这里把用户名放进去
                .setIssuedAt(new Date())                            //设置token的生成时间
                .setId(user.getId() + "")               //设置用户id为token  id      ''是因为用户id是int类型，需要转换为字符串类型
                .setClaims(map)                                     //map中可以存放用户的角色权限信息
                .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) //设置token过期时间，当前时间加一天就是时效为一天过期
                .signWith(SignatureAlgorithm.HS256, "ycj123456")     //签名部分，设置HS256加密方式和加密密码,ycj123456是自定义的密码
                .compact();
        return token;

    }
}
