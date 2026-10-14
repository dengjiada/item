package com.tianzhou.item.console.shiro;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 自定义 Shiro 令牌，封装 JWT
 */
public class JwtToken implements AuthenticationToken {
    private final String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
