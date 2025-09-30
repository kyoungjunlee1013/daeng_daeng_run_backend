// src/main/java/com/daengdaengrun/daengdaengrunbackend/global/jwt/JwtUtil.java

package com.daengdaengrun.daengdaengrunbackend.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j(topic = "JwtUtil") // 로그에 출처(클래스 이름)를 남기기 위한 Lombok 어노테이션
@Component // 이 클래스를 Spring 컨테이너에 Bean으로 등록하여, 다른 곳에서 주입받아 사용할 수 있게 함
public class JwtUtil {

    // JWT를 생성하고 검증할 때 사용할 비밀 키 (HMAC-SHA 알고리즘용)
    private final Key key;
    // 액세스 토큰의 유효 기간 (밀리초 단위)
    private final long accessTokenValidityInMilliseconds;

    /**
     * 생성자: application.properties에 설정한 값들을 주입받아 초기화합니다.
     * @param secretKey JWT 비밀 키 (application.properties의 'jwt.secret.key' 값)
     * @param accessTokenValidityInSeconds 액세스 토큰 유효 시간 (초 단위)
     */
    public JwtUtil(
            @Value("${jwt.secret.key}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds) {
        // 주입받은 secretKey 문자열을 HMAC-SHA 알고리즘에 맞는 Key 객체로 변환
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        // 유효 시간을 초에서 밀리초로 변환하여 저장
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
    }

    /**
     * 사용자의 이메일을 받아서 Access Token을 생성하는 메소드
     * @param email 사용자의 이메일
     * @return 생성된 JWT 문자열
     */
    public String createAccessToken(String email) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email) // 토큰의 주체 (누구의 토큰인지, 보통 이메일이나 ID를 넣음)
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 Secret Key
                .compact(); // JWT 문자열로 압축
    }

    /**
     * 토큰에서 사용자 정보(이메일)를 추출하는 메소드
     * @param token 검증된 JWT 토큰 문자열
     * @return 토큰에 담겨있던 사용자의 이메일
     */
    public String getUserInfoFromToken(String token) {
        // 토큰을 파싱하여 Claims(정보 조각)을 얻어내고, 그 안에서 Subject(주체, 즉 이메일)를 반환
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * 토큰의 유효성을 검증하는 메소드
     * @param token 검증할 JWT 토큰 문자열
     * @return 토큰이 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // setSigningKey로 비밀 키를 설정하고, 토큰을 파싱해봄
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true; // 성공하면 유효한 토큰
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false; // 위에서 예외(Exception)가 발생했다면 유효하지 않은 토큰
    }
}