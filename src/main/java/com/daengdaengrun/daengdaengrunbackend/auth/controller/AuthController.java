package com.daengdaengrun.daengdaengrunbackend.auth.controller;

import com.daengdaengrun.daengdaengrunbackend.auth.dto.LoginRequestDto;
import com.daengdaengrun.daengdaengrunbackend.auth.service.AuthService;
import com.daengdaengrun.daengdaengrunbackend.global.error.dto.ApiResponseDto;
import com.daengdaengrun.daengdaengrunbackend.global.error.dto.LoginResponseDto;
import com.daengdaengrun.daengdaengrunbackend.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
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

    /**
     * 로그아웃 처리
     * @param request HttpServletRequest: 헤더에서 토큰을 추출하기 위해 필요
     * @return 성공 메시지
     */

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            authService.logout(accessToken);
        }
        //2. ApiResponseDto를 사용하여 응답 반환
        return ResponseEntity.ok(new ApiResponseDto<>(null, "로그아웃 되었습니다."));
    }

}

