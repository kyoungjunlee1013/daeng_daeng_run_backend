package com.daengdaengrun.daengdaengrunbackend.global.error.dto;

import lombok.Getter;

@Getter
public class ApiResponseDto<T> {
    private final boolean success;
    private final T data;
    private final String message;

    //성공 시 사용하는 생성자
    public ApiResponseDto(T data, String message){
        this.success = true;
        this.data = data;
        this.message = message;
    }

    //실패 시 사용하는 생성자가 boolean인 이유는 authservice에서 예외를 던지기 때문
    public ApiResponseDto(boolean success, String message){
        this.success = success;
        this.data = null;
        this.message = message;
    }

}
