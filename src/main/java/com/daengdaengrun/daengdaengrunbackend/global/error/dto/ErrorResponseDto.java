package com.daengdaengrun.daengdaengrunbackend.global.error.dto;

import lombok.Getter;

@Getter
public class ErrorResponseDto {
    private final String message;
    private final int statusCode;

    public ErrorResponseDto(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
