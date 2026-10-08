package com.tianzhou.item.app.utils;

import com.alibaba.fastjson2.JSON;
import com.tianzhou.item.module.entity.UserTokenPayload;
import com.tianzhou.item.module.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class loginResolverUtils {
    @Autowired
    private JwtUtils jwtUtils;

    private loginResolverUtils() {
    }

    //校验登录
    public Long requiredLogin(HttpServletRequest request) {
        String sign = request.getHeader("Authorization");
        if (sign == null || !sign.startsWith("Bearer ")) {
            throw new RuntimeException("this user has not logged in yet");
        }

        sign = sign.substring(7);

        //解析sign
        String parseToken = jwtUtils.parseToken(sign);

        //反序列化token
        UserTokenPayload tokenPayload = JSON.parseObject(parseToken, UserTokenPayload.class);
        return tokenPayload.getUserId();
    }

}
