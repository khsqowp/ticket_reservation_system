package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.UserDomain;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 회원가입 요청을 위한 DTO (Data Transfer Object)
 */
@Getter
@NoArgsConstructor
public class UserSignupRequestDTO {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Builder
    public UserSignupRequestDTO(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * DTO를 UserDomain 엔티티로 변환하는 메소드
     * @return UserDomain
     */
    public UserDomain toEntity() {
        return UserDomain.builder()
                .email(this.email)
                .password(this.password) // 실제 프로젝트에서는 비밀번호 암호화가 필요합니다.
                .name(this.name)
                .build();
    }
}