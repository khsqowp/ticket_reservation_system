package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.config.jwt.JwtUtil;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() {
        // given
        UserSignupRequestDTO requestDTO = new UserSignupRequestDTO();
        ReflectionTestUtils.setField(requestDTO, "email", "test@example.com");
        ReflectionTestUtils.setField(requestDTO, "password", "password123");
        ReflectionTestUtils.setField(requestDTO, "name", "테스트유저");

        String encodedPassword = "encodedPassword";
        given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(requestDTO.getPassword())).willReturn(encodedPassword);
        given(userRepository.save(any(UserDomain.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        UserDomain result = userService.signup(requestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(requestDTO.getEmail());
        assertThat(result.getPassword()).isEqualTo(encodedPassword); // 암호화된 비밀번호 확인
        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_success() {
        // given
        UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test@example.com", "password123");
        String encodedPassword = "encodedPassword";
        UserDomain user = UserDomain.builder()
                .email(requestDTO.getEmail())
                .password(encodedPassword)
                .role(UserRoleEnum.USER)
                .build();
        String dummyToken = "dummy-jwt-token";

        given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDTO.getPassword(), encodedPassword)).willReturn(true);
        given(jwtUtil.createToken(user.getEmail(), user.getRole())).willReturn(dummyToken);

        // when
        String token = userService.login(requestDTO);

        // then
        assertThat(token).isEqualTo(dummyToken);
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void login_fail_wrong_password() {
        // given
        UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test@example.com", "wrong_password");
        String encodedPassword = "encodedPassword";
        UserDomain user = UserDomain.builder()
                .email(requestDTO.getEmail())
                .password(encodedPassword)
                .role(UserRoleEnum.USER)
                .build();

        given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDTO.getPassword(), encodedPassword)).willReturn(false); // 비밀번호 불일치 상황 모의

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userService.login(requestDTO));
    }
}