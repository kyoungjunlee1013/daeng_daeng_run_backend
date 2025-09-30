package com.daengdaengrun.daengdaengrunbackend.global.error.dto.handler;

import com.daengdaengrun.daengdaengrunbackend.global.error.dto.ApiResponseDto;
import com.daengdaengrun.daengdaengrunbackend.global.error.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 이 클래스에서 처리하도록 설정
public class GlobalExceptionHandler {

    //IllegalArgumentException 예외가 발생했을 때 이 메소드가 호출됨
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDto<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
      ApiResponseDto<?> response = new ApiResponseDto<>(false, ex.getMessage());
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    // 예상치 못한 모든 예외를 처리하기 위한 핸들러 (서버 에러)
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex) {
        ErrorResponseDto response = new ErrorResponseDto("서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
        // 500 Internal Server Error 상태 코드와 함께 응답 반환
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
