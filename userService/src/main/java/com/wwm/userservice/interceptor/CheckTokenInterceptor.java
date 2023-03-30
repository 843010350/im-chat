package com.wwm.userservice.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwm.nettycommon.entity.User;
import com.wwm.nettycommon.response.EnumCode;
import com.wwm.nettycommon.response.Result;
import com.wwm.nettycommon.utils.ThreadLocalUtils;
import io.jsonwebtoken.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@Component
public class CheckTokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //关于浏览器的请求预检.在跨域的情况下，非简单请求会先发起一次空body的OPTIONS请求，称为"预检"请求，用于向服务器请求权限信息，等预检请求被成功响应后，才发起真正的http请求。
        String method = request.getMethod();
        if("OPTIONS".equalsIgnoreCase(method)){
            return true;
        }
//        String token = request.getParameter("token");放入params才能用这个，放hearder用getHearder
        String token = request.getHeader("token");
        if(Objects.isNull(token) || token.equals("null")){
            Result<Object> result = Result.fail(EnumCode.NO_LOGIN.getCode(), "请先登录");
            doResponse(response,result);
        }else{
            try {
                JwtParser parser = Jwts.parser();
                parser.setSigningKey("ycj123456"); //解析token的SigningKey必须和生成token时设置密码一致
                //如果token检验通过（密码正确，有效期内）则正常执行，否则抛出异常
                Jws<Claims> claimsJws = parser.parseClaimsJws(token);
                Claims body = claimsJws.getBody();
                Object uid = body.get("id");
                Object username1 = body.get("username");
                if(Objects.isNull(uid) || Objects.isNull(username1) || uid.equals("") || username1.equals("")){
                    Result<Object> result = Result.fail(EnumCode.LOGIN_EXPIRED.getCode(), EnumCode.LOGIN_EXPIRED.getDesc());
                    doResponse(response,result);
                    return false;
                }
                Integer id = (Integer) uid;
                String username = (String) username1;
                User user = new User();
                user.setId(id);
                user.setUsername(username);
                ThreadLocalUtils.userThreadLocal.set(user);


                return true;//true就是验证通过，放行
            }catch (ExpiredJwtException e){
                Result<Object> result = Result.fail(EnumCode.LOGIN_EXPIRED.getCode(), EnumCode.LOGIN_EXPIRED.getDesc());
                doResponse(response,result);
            }catch (UnsupportedJwtException e){
                Result<Object> result = Result.fail(EnumCode.INVALID_TOKEN.getCode(), EnumCode.INVALID_TOKEN.getDesc());
                doResponse(response,result);
            }catch (Exception e){
                Result<Object> result = Result.fail(EnumCode.FAIL.getCode(), EnumCode.INVALID_TOKEN.getDesc());
                doResponse(response,result);
            }
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.userThreadLocal.remove();
    }

    //没带token或者检验失败响应给前端
    private void doResponse(HttpServletResponse response,Result result) throws IOException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(result);
        out.print(s);
        out.flush();
        out.close();
    }



}
