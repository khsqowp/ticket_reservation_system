package com.example.ticket_reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 전역 예외 처리를 담당하는 클래스
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * IllegalArgumentException을 처리하는 핸들러
     * @param e 발생한 IllegalArgumentException
     * @return 에러 메시지와 400 Bad Request 상태 코드를 담은 ResponseEntity
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * IllegalStateException을 처리하는 핸들러
     * 주로 상태가 올바르지 않은 경우(예: 이미 예약된 좌석을 다시 예약)에 발생합니다.
     * @param e 발생한 IllegalStateException
     * @return 에러 메시지와 409 Conflict 상태 코드를 담은 ResponseEntity
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        Map<String, String> errorResponse = Map.of("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}