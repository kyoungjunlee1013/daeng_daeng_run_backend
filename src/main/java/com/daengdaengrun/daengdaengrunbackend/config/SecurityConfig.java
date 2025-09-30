// src/main/java/com/daengdaengrun/daengdaengrunbackend/config/SecurityConfig.java

package com.daengdaengrun.daengdaengrunbackend.config;

// ... (여러 import 구문)

import com.daengdaengrun.daengdaengrunbackend.auth.handler.OAuth2LoginSuccessHandler;
import com.daengdaengrun.daengdaengrunbackend.auth.security.PrincipalDetailsService;
import com.daengdaengrun.daengdaengrunbackend.auth.service.CustomOAuth2UserService;
import com.daengdaengrun.daengdaengrunbackend.global.jwt.JwtAuthenticationFilter;
import com.daengdaengrun.daengdaengrunbackend.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// 1. 클래스 레벨 어노테이션: "이 클래스는 보안 설정을 총괄하는 특별한 파일입니다!"
@Configuration // 이 클래스가 Spring의 설정 파일임을 알립니다. Bean들을 정의하는 곳입니다.
@EnableWebSecurity // Spring Security의 웹 보안 기능을 활성화합니다. 이게 메인 스위치입니다.
@RequiredArgsConstructor // final 필드들을 사용하는 생성자를 자동으로 만들어, 의존성 주입을 간편하게 합니다.
public class SecurityConfig {

    // 2. 의존성 주입: 보안 설정을 구성하는 데 필요한 '재료' 또는 '부품'들입니다.
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtUtil jwtUtil;
    private final PrincipalDetailsService principalDetailsService;

    // 3. Bean 등록: 프로젝트 전역에서 사용할 공용 '부품'들을 만듭니다.

    // '비밀번호 암호화' 부품을 만드는 기계
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 'JWT 문지기 필터' 부품을 만드는 기계
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, principalDetailsService);
    }

    // 4. securityFilterChain: 실제 보안 규칙을 설정하는 '메인 메소드'
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // --- 4-A: JWT 사용을 위한 기본 설정 (Stateless 설정) ---
        http
                // CSRF(Cross-Site Request Forgery) 보호 기능 비활성화.
                // 세션/쿠키 기반의 공격을 막는 기술인데, 우리는 JWT 토큰 기반이므로 불필요합니다.
                .csrf(csrf -> csrf.disable())

                // 세션 관리 정책을 STATELESS(상태를 저장하지 않음)로 설정.
                // 서버가 사용자의 로그인 상태를 세션에 기억하지 않으며, 모든 요청은 오직 JWT로만 인증합니다.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // --- 4-B: API 경로별 접근 권한 설정 ---
        http
                .authorizeHttpRequests(auth -> auth
                        // 아래 경로들은 로그인하지 않은 사용자도 접근할 수 있도록 허용(permitAll).
                        .requestMatchers("/api/auth/**", "/api/users/signup").permitAll()
                        // 소셜 로그인을 시작하는 경로도 모두 허용.
                        .requestMatchers("/oauth2/**").permitAll()
                        // 위에서 허용한 경로들을 제외한 나머지 모든 경로는 반드시 인증(로그인)이 필요함.
                        .anyRequest().authenticated()
                );

        // --- 4-C: 소셜 로그인(OAuth 2.0) 설정 ---
        http
                .oauth2Login(oauth2 -> oauth2
                        // 소셜 로그인 성공 시, 우리가 만든 oAuth2LoginSuccessHandler를 사용하여 후처리(JWT 발급 등)를 하도록 지정.
                        .successHandler(oAuth2LoginSuccessHandler)
                        // 소셜 로그인 성공 후 사용자 정보를 가져오는 로직을 처리할 담당자로, 우리가 만든 customOAuth2UserService를 지정.
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );

        // --- 4-D: 우리가 만든 JWT 문지기 필터 등록 ---
        http
                // Spring Security의 수많은 보안 필터들 중에서, 우리가 만든 'JwtAuthenticationFilter'를
                // 'UsernamePasswordAuthenticationFilter'(일반적인 아이디/비밀번호 로그인 처리 필터) 바로 앞에 배치.
                // 이렇게 해야 로그인 이외의 모든 요청에 대해 JWT 검사를 가장 먼저 수행할 수 있습니다.
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 설정이 완료된 HttpSecurity 객체를 빌드하여 반환
        return http.build();
    }
}