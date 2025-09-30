package com.daengdaengrun.daengdaengrunbackend.global.error.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final String token;

    public LoginResponseDto(String token){
        this.token = token;
    }
}
