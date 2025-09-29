package com.daengdaengrun.daengdaengrunbackend.auth.dto;

import com.daengdaengrun.daengdaengrunbackend.user.UserRole;
import com.daengdaengrun.daengdaengrunbackend.user.UserStatus;
import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
    // registrationId(google, kakao 등)에 따라 다른 메소드를 호출하여 OAuthAttributes 객체를 생성
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if("kakao".equals(registrationId)){
            return ofKakao("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    //OAuthAttributes에서 user 엔터티를 생성하는 메소드
    public User toEntity() {
        return User.builder()
                .nickname(name) // 닉네임은 소셜 프로필 이름
                .email(email)
                //비밀번호는 소셜 로그인 시 의미 없으므로 임의의 값으로 설정
                .password(UUID.randomUUID().toString())
                .profileImageUrl(picture)
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

}
