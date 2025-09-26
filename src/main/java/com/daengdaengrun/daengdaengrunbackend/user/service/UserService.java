package com.daengdaengrun.daengdaengrunbackend.user.service;

import com.daengdaengrun.daengdaengrunbackend.user.dto.UserSignupRequestDto;
import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import com.daengdaengrun.daengdaengrunbackend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional //이 메소드가 실행될 때 트렌잭션을 시작하고 끝나면 커밋 , 에러 발생 시 롤백
    public void signup(UserSignupRequestDto requestDto) {

        if(userRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if(userRepository.existsByNickname(requestDto.getNickname())){
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User newUser = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword) // requestDto/getPassword를 사용하지 않는 이유는 암호화가 안되어있기 때문이다.
                .nickname(requestDto.getNickname())
                .build();

        userRepository.save(newUser);
    }

}
