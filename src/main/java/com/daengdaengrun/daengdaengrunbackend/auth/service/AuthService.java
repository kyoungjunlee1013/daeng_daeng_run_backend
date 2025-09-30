package com.daengdaengrun.daengdaengrunbackend.auth.service;

import com.daengdaengrun.daengdaengrunbackend.auth.dto.LoginRequestDto;
import com.daengdaengrun.daengdaengrunbackend.global.jwt.JwtUtil;
import com.daengdaengrun.daengdaengrunbackend.global.redis.RedisUtil;
import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import com.daengdaengrun.daengdaengrunbackend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    /**
     * 이메일/비밀번호로 로그인하는 비즈니스 로직
     * @param loginRequestDto 이메일, 비밀번호 DTO
     * @return 생성된 Access Token (JWT)
     */
    @Transactional(readOnly = true)
    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        // 1. 이메일로 DB에서 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없거나 이메일 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 확인 (입력된 평문 비밀번호와 DB의 암호화된 비밀번호 비교)
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 3. 로그인 성공 시 JWT 생성하여 반환
            return jwtUtil.createAccessToken(user.getEmail());
        } else {
            // 비밀번호 불일치 시
            throw new IllegalArgumentException("등록된 사용자가 없거나, 이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    /**
     * 비밀번호 찾기 요청을 처리하는 비즈니스 로직
     * @param email 비밀번호를 재설정할 사용자의 이메일
     */
    @Transactional(readOnly = true)
    public void requestPasswordReset(String email) {
        // 1. 이메일로 사용자가 존재하는지 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        // 2. 임시 토큰 생성
        String resetToken = UUID.randomUUID().toString();
        // 3. Redis에 토큰 저장 (키: "RT:"+토큰, 값: 이메일, 유효시간: 10분)
        redisUtil.setDataExpire("RT:" + resetToken, user.getEmail(), 60 * 10);

        // TODO: 실제로 사용자 이메일로 비밀번호 재설정 링크(토큰 포함)를 보내는 로직이 필요합니다.
        // 현재는 개발 편의를 위해 콘솔에 토큰을 출력합니다.
        System.out.println("비밀번호 재설정 토큰: " + resetToken + " (이메일로 전송되었다고 가정)");
    }

    /**
     * 새로운 비밀번호로 재설정하는 비즈니스 로직
     * @param resetToken 이메일로 받은 임시 토큰
     * @param newPassword 사용자가 새로 설정할 비밀번호
     */
    @Transactional
    public void resetPassword(String resetToken, String newPassword) {
        // 1. Redis에서 토큰으로 이메일 정보 가져오기
        String email = redisUtil.getData("RT:" + resetToken);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        // 2. 이메일로 사용자 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        // 3. 새 비밀번호를 암호화하여 User 엔티티의 password 필드 업데이트
        user.setPassword(passwordEncoder.encode(newPassword));
        // @Transactional 어노테이션 덕분에 메소드가 끝나면 변경된 내용(Dirty Checking)이 자동으로 DB에 UPDATE 됩니다.
        // 따라서 userRepository.save(user)를 호출할 필요가 없습니다.

        // 4. 사용 완료된 토큰은 Redis에서 삭제
        redisUtil.deleteData("RT:" + resetToken);
    }
}