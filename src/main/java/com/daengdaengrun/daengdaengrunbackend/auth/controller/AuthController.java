package com.daengdaengrun.daengdaengrunbackend.auth.controller;

import com.daengdaengrun.daengdaengrunbackend.auth.dto.LoginRequestDto;
import com.daengdaengrun.daengdaengrunbackend.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 이메일/비밀번호 기반 로그인 API
     * @param loginRequestDto 이메일과 비밀번호를 담은 DTO
     * @return 성공 시 JWT 토큰, 실패 시 에러 메시지
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            String token = authService.login(loginRequestDto);
            return ResponseEntity.ok(Map.of("token", token, "message", "로그인 성공"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("message", e.getMessage())); // 401 Unauthorized
        }
    }

    /**
     * 비밀번호 찾기(재설정) 요청 API
     * @param body 요청 본문에 담긴 email
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/password/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> body) {
        try {
            authService.requestPasswordReset(body.get("email"));
            return ResponseEntity.ok("비밀번호 재설정 이메일이 발송되었습니다. (현재는 콘솔 확인)");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage()); // 404 Not Found
        }
    }

    /**
     * 비밀번호 재설정 API
     * @param token URL 쿼리 파라미터로 전달된 재설정 토큰
     * @param body 요청 본문에 담긴 newPassword
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
        try {
            authService.resetPassword(token, body.get("newPassword"));
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage()); // 400 Bad Request
        }
    }
}