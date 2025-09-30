// src/main/java/com/daengdaengrun/daengdaengrunbackend/auth/security/PrincipalDetails.java

package com.daengdaengrun.daengdaengrunbackend.auth.security;

import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Spring Security가 사용자의 인증/인가 정보를 담는 표준 인터페이스(UserDetails, OAuth2User)를 구현한 클래스.
 * 우리 서비스의 User 엔티티를 Spring Security가 이해할 수 있도록 변환해주는 '어댑터' 역할을 합니다.
 */
public class PrincipalDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;

    // 일반 로그인 시 사용되는 생성자
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // OAuth2.0 소셜 로그인 시 사용되는 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // UserDetails 인터페이스 구현: 사용자의 권한 목록을 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // user.getRole()은 UserRole Enum 타입이므로, Spring Security가 이해하는 SimpleGrantedAuthority로 변환해줍니다.
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // UserDetails 인터페이스 구현: 사용자의 비밀번호를 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // UserDetails 인터페이스 구현: 사용자를 식별하는 주요 정보(ID)를 반환 (우리는 이메일을 ID로 사용)
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // --- 이하 계정 상태 관련 메소드들 ---
    // User 엔티티의 status 필드를 사용하여 더 정교하게 구현할 수 있으나, 지금은 간단히 true로 설정합니다.

    // 계정이 만료되지 않았는지 리턴 (true: 만료안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 리턴 (true: 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호가 만료되지 않았는지 리턴 (true: 만료안됨)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화(사용가능)인지 리턴 (true: 활성화)
    @Override
    public boolean isEnabled() {
        return true;
    }


    // === OAuth2User 인터페이스 구현 ===

    // 소셜 로그인 제공자로부터 받은 원본 사용자 속성(attributes)을 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2 사용자의 주요 식별자(PK)를 반환 (우리는 User의 ID를 사용)
    @Override
    public String getName() {
        // 일반적으로 name attribute key를 사용하지만, 우리 시스템에서는 User ID를 고유 식별자로 사용합니다.
        return String.valueOf(user.getId());
    }
}