package com.example.ticket_reservation_system.dto;

import lombok.Getter;

/**
 * 사용자 로그인 성공 응답을 위한 DTO
 */
@Getter
public class UserLoginResponseDTO {
    private final String message;
    private final String token; // 현재는 임시 토큰, 추후 JWT(JSON Web Token)으로 대체될 예정입니다.

    public UserLoginResponseDTO(String message, String token) {
        this.message = message;
        this.token = token;
    }
}