package com.daengdaengrun.daengdaengrunbackend.user.controller;

import com.daengdaengrun.daengdaengrunbackend.global.error.dto.ApiResponseDto;
import com.daengdaengrun.daengdaengrunbackend.user.dto.UserSignupRequestDto;
import com.daengdaengrun.daengdaengrunbackend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 이 클래스가 REST API의 컨트롤러임을 선언
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<Void>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity.ok(new ApiResponseDto<>(null, "회원가입이 성공적으로 완료되었습니다."));
    }
}
