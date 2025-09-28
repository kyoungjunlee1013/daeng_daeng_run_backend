// src/main/java/com/daengdaengrun/daengdaengrunbackend/auth/handler/OAuth2LoginSuccessHandler.java
package com.daengdaengrun.daengdaengrunbackend.auth.handler;

import com.daengdaengrun.daengdaengrunbackend.global.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // CustomOAuth2UserService에서 DB에 저장/업데이트한 사용자의 이메일을 가져옵니다.
        String email = oAuth2User.getAttribute("email");
        if (email == null && oAuth2User.getAttribute("kakao_account") instanceof java.util.Map) {
            java.util.Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        if (email != null) {
            String accessToken = jwtUtil.createAccessToken(email);
            // 프론트엔드로 리다이렉트할 URL을 생성합니다. 토큰을 쿼리 파라미터로 추가합니다.
            String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth/callback")
                    .queryParam("token", accessToken)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            // 생성된 URL로 리다이렉트시킵니다.
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            // 이메일 정보를 가져올 수 없는 경우 에러 처리
            log.error("OAuth2 Login 성공 후 이메일 정보를 가져올 수 없습니다.");
            String targetUrl = "http://localhost:5173/login?error=email_not_found";
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}