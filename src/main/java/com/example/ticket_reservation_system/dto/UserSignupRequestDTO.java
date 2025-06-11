package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequestDTO {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String name;

    @Builder.Default // [수정] Builder 기본값 설정을 위해 추가
    private boolean admin = false;
    @Builder.Default // [수정] Builder 기본값 설정을 위해 추가
    private String adminToken = "";

    public UserDomain toEntity(PasswordEncoder passwordEncoder, UserRoleEnum role) {
        return UserDomain.builder()
                .email(this.email)
                .password(passwordEncoder.encode(this.password))
                .name(this.name)
                .role(role)
                .build();
    }
}