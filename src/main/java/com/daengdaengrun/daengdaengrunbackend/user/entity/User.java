// src/main/java/com/daengdaengrun/daengdaengrunbackend/user/User.java
package com.daengdaengrun.daengdaengrunbackend.user.entity;

import com.daengdaengrun.daengdaengrunbackend.user.UserRole;
import com.daengdaengrun.daengdaengrunbackend.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS") // SQL 스키마와 동일하게 테이블 이름 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성/수정 시간을 자동으로 관리하도록 설정
@Builder // 원래 아래에 User 메소드가 아닌 이 곳에 둔 이유는 매개변수로 받는 거 아니더라도 에러가 안생기게
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 자동으로 만듭니다. (클래스 레벨 @Builder에 필요)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // SQL 컬럼명과 매핑
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50) // 길이 제한 추가
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl; // Java에서는 CamelCase로 작성

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 저장
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @CreatedDate // 엔티티 생성 시 시간이 자동으로 저장됨
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // 엔티티 수정 시 시간이 자동으로 저장됨
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = UserRole.USER; // 기본 역할은 USER로 설정
        this.status = UserStatus.ACTIVE; // 기본 상태는 ACTIVE로 설정
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}