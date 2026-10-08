package com.tianzhou.item.module.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;

    private JwtUtils(){}

    //APP端过期时间
    private static final long APP_EXPIRE = 7 * 24 * 3600 * 1000L;

    //把字符串类型的密钥转换成Secret Key类型
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //生成jwt
    public String generateAppToken(String payLoad) {
        return Jwts.builder()
                .subject(payLoad)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + APP_EXPIRE))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    //解析jwt
    public String parseToken(String token) {
        Claims payload = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return payload.getSubject();
    }
}
