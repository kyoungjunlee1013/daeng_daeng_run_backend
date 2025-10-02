// src/main/java/com/daengdaengrun/daengdaengrunbackend/global/jwt/JwtAuthenticationFilter.java

package com.daengdaengrun.daengdaengrunbackend.global.jwt;

import com.daengdaengrun.daengdaengrunbackend.auth.security.PrincipalDetailsService;
import com.daengdaengrun.daengdaengrunbackend.global.redis.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final PrincipalDetailsService principalDetailsService;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청 헤더에서 "Bearer " 토큰을 추출합니다.
        String token = resolveTokenFromRequest(request);

        // 2. 토큰이 존재하고 유효한지 확인합니다.
        if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {

            // 3. [개선된 부분] Redis에 해당 토큰이 "logout"으로 저장되어 있는지 (블랙리스트에 있는지) 확인합니다.
            String isLoggedOut = redisUtil.getData(token);

            if (isLoggedOut == null) { // 블랙리스트에 없다면 (즉, 로그아웃된 토큰이 아니라면)
                // 4. 토큰에서 사용자 정보(이메일)를 가져옵니다.
                String email = jwtUtil.getUserInfoFromToken(token);
                // 5. 이메일로 DB에서 사용자 정보를 조회하여 UserDetails 객체를 생성합니다.
                UserDetails userDetails = principalDetailsService.loadUserByUsername(email);
                // 6. 인증 정보 객체(Authentication)를 생성합니다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // 7. Spring Security의 컨텍스트에 인증 정보를 저장하여, 이 요청 동안 인증된 사용자로 인식되게 합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 8. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 "Bearer " 접두사를 제거하고 순수한 토큰 값만 추출하는 메소드
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // StringUtils.hasText()는 null이 아니고, 길이가 0보다 크고, 공백만으로 이루어지지 않았는지 확인합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}