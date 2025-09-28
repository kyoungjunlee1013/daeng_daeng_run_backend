// src/main/java/com/daengdaengrun/daengdaengrunbackend/config/SecurityConfig.java
package com.daengdaengrun.daengdaengrunbackend.config;

import com.daengdaengrun.daengdaengrunbackend.auth.handler.OAuth2LoginSuccessHandler;
import com.daengdaengrun.daengdaengrunbackend.auth.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// ... (기존 import)

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // final 필드 주입을 위해 추가
public class SecurityConfig {

    // 우리가 만든 CustomOAuth2UserService와 OAuth2LoginSuccessHandler를 주입받습니다.
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/users/signup").permitAll()
                        .anyRequest().authenticated()
                )
                // --- 👇 [추가] OAuth2 로그인 설정 ---
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 성공 시 이 핸들러를 사용
                        .successHandler(oAuth2LoginSuccessHandler)
                        // 사용자 정보를 처리할 때 이 서비스를 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        return http.build();
    }
}