package com.tianzhou.item.console.interceptor;

import com.alibaba.fastjson2.JSON;
import com.tianzhou.item.console.context.ConsoleContext;
import com.tianzhou.item.module.entity.UserTokenPayload;
import com.tianzhou.item.module.utils.JwtUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ConsoleLoginInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中拿到token
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("console_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        //校验token是否存在
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("this user has not logged in yet");
        }

        //解析token
        String parseToken = jwtUtils.parseToken(token);

        //反序列化
        UserTokenPayload tokenPayload = JSON.parseObject(parseToken, UserTokenPayload.class);

        // 将userId存入 ThreadLocal 中
        ConsoleContext.set(tokenPayload.getUserId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 请求结束，清理 ThreadLocal，防止内存泄漏
        ConsoleContext.remove();
    }
}
