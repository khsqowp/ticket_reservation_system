package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_success() {
        // given: 이러한 데이터가 주어졌을 때
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .build();

        UserDomain userToSave = requestDTO.toEntity();
        UserDomain savedUser = UserDomain.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .build();

        // userRepository.findByEmail()이 호출되면 Optional.empty()를 반환하도록 설정
        given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.empty());
        // userRepository.save()가 호출되면 savedUser를 반환하도록 설정
        given(userRepository.save(any(UserDomain.class))).willReturn(savedUser);

        // when: 이 메소드를 실행하면
        UserDomain result = userService.signup(requestDTO);

        // then: 이러한 결과가 나와야 한다
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(requestDTO.getEmail());
        assertThat(result.getName()).isEqualTo(requestDTO.getName());

        // userRepository.findByEmail과 save 메소드가 각각 한 번씩 호출되었는지 검증
        verify(userRepository).findByEmail(requestDTO.getEmail());
        verify(userRepository).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 중복")
    void signup_fail_duplicate_email() {
        // given: 이러한 데이터가 주어졌을 때
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .name("테스트유저")
                .build();

        // userRepository.findByEmail()이 호출되면 이미 존재하는 UserDomain 객체를 포함한 Optional을 반환
        given(userRepository.findByEmail(requestDTO.getEmail())).willReturn(Optional.of(requestDTO.toEntity()));

        // when & then: 이 메소드를 실행하면 IllegalArgumentException이 발생해야 한다
        assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(requestDTO);
        }, "이미 사용 중인 이메일입니다.");

        verify(userRepository).findByEmail(requestDTO.getEmail());
    }
}