package com.daengdaengrun.daengdaengrunbackend.auth.controller;

import com.daengdaengrun.daengdaengrunbackend.auth.dto.LoginRequestDto;
import com.daengdaengrun.daengdaengrunbackend.auth.service.AuthService;
import com.daengdaengrun.daengdaengrunbackend.global.error.dto.ApiResponseDto;
import com.daengdaengrun.daengdaengrunbackend.global.error.dto.LoginResponseDto;
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
    public ResponseEntity<ApiResponseDto<LoginResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
            String token = authService.login(loginRequestDto);
            return ResponseEntity.ok(new ApiResponseDto<>(new LoginResponseDto(token),"로그인 성공"));
        }
    /**
     * 비밀번호 찾기(재설정) 요청 API
     * @param body 요청 본문에 담긴 email
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/password/request")
    public ResponseEntity<ApiResponseDto<Void>> requestPasswordReset(@RequestBody Map<String, String> body) {
            authService.requestPasswordReset(body.get("email"));
            return ResponseEntity.ok(new ApiResponseDto<>(null, "비밀번호 재설정 이메일이 발송되었습니다. (현재는 콘솔 확인)"));
        }

    /**
     * 비밀번호 재설정 API
     * @param token URL 쿼리 파라미터로 전달된 재설정 토큰
     * @param body 요청 본문에 담긴 newPassword
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponseDto<Void>> resetPassword(@RequestParam String token, @RequestBody Map<String, String> body) {
            authService.resetPassword(token, body.get("newPassword"));
            return ResponseEntity.ok(new ApiResponseDto<>(null, "비밀번호가 성공적으로 변경되었습니다."));
    }
}

