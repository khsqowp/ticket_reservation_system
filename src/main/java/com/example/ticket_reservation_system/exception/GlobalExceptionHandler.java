package com.example.ticket_reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 전역 예외 처리를 담당하는 클래스
 * @RestControllerAdvice 어노테이션을 통해 모든 @RestController에서 발생하는 예외를 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException을 처리하는 핸들러
     * 서비스 계층에서 발생하는 대부분의 비즈니스 유효성 검사 실패는 이 핸들러에 의해 처리됩니다.
     * @param e 발생한 IllegalArgumentException
     * @return 에러 메시지와 400 Bad Request 상태 코드를 담은 ResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        // 클라이언트에게 반환할 에러 메시지를 Map 형태로 구성합니다.
        Map<String, String> errorResponse = Map.of("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}