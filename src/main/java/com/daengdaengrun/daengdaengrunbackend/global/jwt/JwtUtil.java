
package com.daengdaengrun.daengdaengrunbackend.global.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Spring 컨테이너에 Bean으로 등록
public class JwtUtil {

    private final Key key;
    private final long accessTokenValidityInMilliseconds;

    // 생성자를 통해 application.properties에 설정한 값들을 주입받음
    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
    }

    // 이메일을 받아서 Access Token을 생성하는 메소드
    public String createAccessToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email) // 토큰의 주체 (사용자 이메일)
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 Secret Key
                .compact();
    }
}