package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserController에 대한 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("회원가입 API 테스트")
    class SignupApiTest {
        // ... (이전과 동일)
        @Test
        @DisplayName("성공")
        void signup_api_success() throws Exception {
            UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                    .email("integration.test@example.com")
                    .password("password1234")
                    .name("통합테스트유저")
                    .build();

            mockMvc.perform(post("/api/users/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value(requestDTO.getEmail()));
        }

        @Test
        @DisplayName("실패 - 잘못된 이메일 형식")
        void signup_api_fail_invalid_email() throws Exception {
            UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                    .email("invalid-email-format")
                    .password("password1234")
                    .name("통합테스트유저")
                    .build();

            mockMvc.perform(post("/api/users/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("로그인 API 테스트")
    class LoginApiTest {

        @BeforeEach
        void setUp() {
            userRepository.save(UserDomain.builder()
                    .email("testuser@example.com")
                    .password("password123")
                    .name("테스트유저")
                    .build());
        }

        @Test
        @DisplayName("성공")
        void login_api_success() throws Exception {
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("testuser@example.com", "password123");

            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                    .andExpect(jsonPath("$.token").exists())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 이메일")
        void login_api_fail_user_not_found() throws Exception {
            // given
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("wronguser@example.com", "password123");

            // when & then
            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest()) // [수정] 500 에러 대신 400 Bad Request를 기대합니다.
                    .andExpect(jsonPath("$.error").value("가입되지 않은 이메일입니다.")) // [추가] 응답 본문의 에러 메시지를 검증합니다.
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void login_api_fail_wrong_password() throws Exception {
            // given
            UserLoginRequestDTO requestDTO = new UserLoginRequestDTO("testuser@example.com", "wrong_password");

            // when & then
            mockMvc.perform(post("/api/users/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest()) // [수정] 500 에러 대신 400 Bad Request를 기대합니다.
                    .andExpect(jsonPath("$.error").value("비밀번호가 일치하지 않습니다.")) // [추가] 응답 본문의 에러 메시지를 검증합니다.
                    .andDo(print());
        }
    }
}
