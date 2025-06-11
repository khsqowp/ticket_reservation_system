package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * UserService에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("회원가입 테스트")
    class SignupTest {
        @Test
        @DisplayName("성공")
        void signup_success() {
            // given
            UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                    .email("test@example.com")
                    .password("password123")
                    .name("테스트유저")
                    .build();

            given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.empty());
            given(userRepository.save(any(UserDomain.class))).willReturn(requestDTO.toEntity());

            // when
            UserDomain result = userService.signup(requestDTO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(requestDTO.getEmail());
            verify(userRepository).save(any(UserDomain.class));
        }

        @Test
        @DisplayName("실패 - 이메일 중복")
        void signup_fail_duplicate_email() {
            // given
            UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                    .email("test@example.com")
                    .password("password123")
                    .name("테스트유저")
                    .build();

            given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(requestDTO.toEntity()));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> userService.signup(requestDTO));
        }
    }


    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {
        @Test
        @DisplayName("성공")
        void login_success() {
            // given
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test@example.com", "password123");
            UserDomain user = UserDomain.builder()
                    .email("test@example.com")
                    .password("password123")
                    .name("테스트유저")
                    .build();
            given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(user));

            // when
            UserDomain result = userService.login(requestDTO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(requestDTO.getEmail());
            verify(userRepository).findByEmail(requestDTO.getEmail());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void login_fail_user_not_found() {
            // given
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("wrong@example.com", "password123");
            given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                userService.login(requestDTO);
            }, "가입되지 않은 이메일입니다.");
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void login_fail_wrong_password() {
            // given
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("test@example.com", "wrong_password");
            UserDomain user = UserDomain.builder()
                    .email("test@example.com")
                    .password("password123") // 저장된 비밀번호는 다름
                    .name("테스트유저")
                    .build();
            given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(user));

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                userService.login(requestDTO);
            }, "비밀번호가 일치하지 않습니다.");
        }
    }
}