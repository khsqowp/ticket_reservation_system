package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.config.jwt.JwtUtil;
import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Security 권한/인가 테스트")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 및 관리자 생성
        UserDomain user = UserDomain.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("password"))
                .name("일반사용자")
                .role(UserRoleEnum.USER)
                .build();
        userRepository.save(user);

        UserDomain admin = UserDomain.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("password"))
                .name("관리자")
                .role(UserRoleEnum.ADMIN)
                .build();
        userRepository.save(admin);

        // JWT 토큰 생성
        userToken = jwtUtil.createToken(user.getEmail(), user.getRole());
        adminToken = jwtUtil.createToken(admin.getEmail(), admin.getRole());
    }

    @Nested
    @DisplayName("공연 등록 API 권한 테스트")
    class PerformanceRegistrationAuthTest {

        @Test
        @DisplayName("성공 - ADMIN 권한으로 공연 등록")
        void registerPerformance_withAdmin_shouldSucceed() throws Exception {
            // given
            PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                    .name("관리자 등록 공연")
                    .place("공연장")
                    .price(10000)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            mockMvc.perform(post("/api/performances")
                            .header(JwtUtil.AUTHORIZATION_HEADER, adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - USER 권한으로 공연 등록")
        void registerPerformance_withUser_shouldFail() throws Exception {
            // given
            PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                    .name("사용자 등록 공연")
                    .place("공연장")
                    .price(10000)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            mockMvc.perform(post("/api/performances")
                            .header(JwtUtil.AUTHORIZATION_HEADER, userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isForbidden()) // 403 Forbidden
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 인증 없이 공연 등록")
        void registerPerformance_withoutAuth_shouldFail() throws Exception {
            // given
            PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                    .name("미인증 등록 공연")
                    .place("공연장")
                    .price(10000)
                    .startTime(LocalDateTime.now().plusDays(1))
                    .build();

            // when & then
            mockMvc.perform(post("/api/performances")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnauthorized()) // 401 Unauthorized
                    .andDo(print());
        }
    }
}