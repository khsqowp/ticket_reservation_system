package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.UserDomain;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 회원가입 성공 응답을 위한 DTO
 */
@Getter
public class UserSignupResponseDTO {
    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;

    private UserSignupResponseDTO(Long id, String email, String name, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
    }

    /**
     * UserDomain 엔티티를 DTO로 변환하는 정적 팩토리 메소드
     * @param userDomain UserDomain 객체
     * @return UserSignupResponseDTO
     */
    public static UserSignupResponseDTO from(UserDomain userDomain) {
        return new UserSignupResponseDTO(
                userDomain.getId(),
                userDomain.getEmail(),
                userDomain.getName(),
                userDomain.getCreatedAt()
        );
    }
}