package com.cookingrecipe.cookingrecipe.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_MS = 3600000;

    // JWT 생성
    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username) // 사용자 정보 설정
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS)) // 만료 시간
                .signWith(SECRET_KEY) // 서명 알고리즘과 비밀 키 설정
                .compact();

        // Redis에 토큰 저장 + 로그아웃 상태 초기화
        redisTemplate.opsForHash().put(username, "token", token);
        redisTemplate.opsForHash().put(username, "loggedOut", false);

        // Redis 만료 시간 설정
        redisTemplate.expire(username, EXPIRATION_MS, TimeUnit.MILLISECONDS);

        return token;
    }

    // JWT 유효성 검증 (Redis 연동)
    public boolean validateToken(String token) {
        try {
            String username = getUsernameFromToken(token);

            // Redis에서 토큰 확인 (opsForValue 사용)
            String redisToken = (String) redisTemplate.opsForValue().get(username);

            if (token.equals(redisToken)) {
                // 토큰의 유효성을 검증
                Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
                return true;
            }

            return false; // Redis에 토큰이 없거나 불일치
        } catch (JwtException | IllegalArgumentException e) {
            return false; // 유효하지 않은 토큰
        }
    }


    // JWT에서 사용자 이름 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
