package com.daengdaengrun.daengdaengrunbackend.user.repository;

import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 이 인터페이스가 데이터베이스와 통신하는 repository임을 spring에게 알려준다.
public interface UserRepository extends JpaRepository<User,Long> {
    // 이메일로 사용자를 찾는 메소드
    Optional<User> findByEmail(String email);
    // 닉네임으로 사용자가 존재하는지 확인하는 메소드
    boolean existsByNickname(String nickname);

    String email(String email);
}
