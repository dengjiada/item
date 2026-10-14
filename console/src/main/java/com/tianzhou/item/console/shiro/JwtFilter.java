package com.tianzhou.item.console.shiro;

import com.alibaba.fastjson2.JSON;
import com.tianzhou.item.module.common.Response;
import com.tianzhou.item.module.enums.ResponseCodeEnum;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.http.MediaType;

import java.io.IOException;

/**
 * 自定义 JWT 过滤器，从 Cookie 中提取 JWT 并执行 Shiro 登录
 */
public class JwtFilter extends AuthenticatingFilter {

    //重写 preHandle，放行 OPTIONS 预检请求 + 手动加跨域头
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 手动加跨域响应头
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        // 如果是 OPTIONS 预检请求，直接返回 200，不进认证逻辑
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        // 非 OPTIONS 请求，走 Shiro 认证
        return super.preHandle(request, response);
    }

    //从 Cookie 中提取 JWT，封装成 JwtToken
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("console_token")) {
                    return new JwtToken(cookie.getValue());
                }
            }
        }
        return null;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        return false; // 总是走 onAccessDenied 逻辑，触发认证
    }

    //认证失败时，直接返回标准 JSON 格式，防止被重定向到登录页
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        try {
            return executeLogin(servletRequest, servletResponse);
        } catch (Exception e) {
            // 认证失败，写入标准 JSON 响应
            writeJsonResponse((HttpServletResponse) servletResponse, Response.fail(ResponseCodeEnum.UNAUTHORIZED));
            return false;
        }
    }

    private void writeJsonResponse(HttpServletResponse response, Response<?> result) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(JSON.toJSONString(result));
        } catch (IOException e) {
            throw new RuntimeException("failed to write JSON response", e);
        }
    }

    /**
     * 重写登录失败处理，直接返回 JSON
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        writeJsonResponse((HttpServletResponse) response, Response.fail(ResponseCodeEnum.UNAUTHORIZED));
        return false;
    }
}
