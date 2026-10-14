package com.tianzhou.item.console.shiro;

import com.alibaba.fastjson2.JSON;
import com.tianzhou.item.module.entity.UserTokenPayload;
import com.tianzhou.item.module.user.entity.User;
import com.tianzhou.item.module.user.mapper.UserMapper;
import com.tianzhou.item.module.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义 Realm，负责从数据库加载用户信息并校验
 */
public class JwtRealm extends AuthorizingRealm {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    //验证 JWT 并查询数据库
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String jwt = (String) authenticationToken.getPrincipal();

        //解析jwt
        String parsedToken = null;
        try {
            parsedToken = jwtUtils.parseToken(jwt);
        } catch (ExpiredJwtException e) {
            throw new IncorrectCredentialsException("token expired");
        } catch (JwtException e) {
            throw new IncorrectCredentialsException("invalid token");
        }

        //反序列化
        UserTokenPayload tokenPayload = JSON.parseObject(parsedToken, UserTokenPayload.class);
        //拿到userId
        Long userId = tokenPayload.getUserId();
        //根据userId查询
        User user = userMapper.getById(userId);
        if (user == null) {
            throw new UnknownAccountException("user not exist");
        }

        //将 userId 作为 Principal 存入 Subject
        return new SimpleAuthenticationInfo(userId, jwt, getName());
    }

    //授权：加载角色和权限
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String userId = (String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        return info;
    }
}
