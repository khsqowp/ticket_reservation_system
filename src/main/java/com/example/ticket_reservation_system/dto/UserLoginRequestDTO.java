package com.example.ticket_reservation_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 로그인 요청을 위한 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor // [수정] 모든 필드를 인자로 받는 생성자를 추가합니다.
public class UserLoginRequestDTO {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    private String password;
}