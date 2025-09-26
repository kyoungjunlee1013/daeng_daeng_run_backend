package com.daengdaengrun.daengdaengrunbackend.auth;

import com.daengdaengrun.daengdaengrunbackend.auth.dto.LoginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service // 이 클래스가 비즈니스 로직을 담당하는 서비스 계층임을 선언
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 만들어주는 Lombok 어노테이션
public class AuthService {
    private String login(LoginRequestDto loginRequestDto) {
        String email =  loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        // TODO: [2단계] 실제 DB와 연동하여 사용자 및 비밀번호 검증 로직을 변경해야 한다.
        if(!"")
    }
}
