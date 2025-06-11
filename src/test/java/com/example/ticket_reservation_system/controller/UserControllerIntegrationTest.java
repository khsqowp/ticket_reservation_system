package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
@SpringBootTest // 스프링 컨텍스트를 모두 로드하여 테스트
@AutoConfigureMockMvc // MockMvc를 주입받아 사용하기 위한 어노테이션
@Transactional // 각 테스트가 끝난 후 데이터베이스를 롤백하여 테스트 간의 독립성 보장
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // 웹 API를 서블릿 컨테이너 구동 없이 테스트하기 위한 객체

    @Autowired
    private ObjectMapper objectMapper; // 객체를 JSON 문자열로 변환하기 위한 객체

    @Test
    @DisplayName("회원가입 API 호출 성공 테스트")
    void signup_api_success() throws Exception {
        // given: 이러한 데이터가 주어졌을 때
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .email("integration.test@example.com")
                .password("password1234")
                .name("통합테스트유저")
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // when & then: /api/users/signup POST 요청을 보냈을 때
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated()) // 응답 상태 코드가 201 Created인지 확인
                .andExpect(jsonPath("$.id").exists()) // 응답 JSON에 id 필드가 존재하는지 확인
                .andExpect(jsonPath("$.email").value(requestDTO.getEmail())) // 이메일 값이 일치하는지 확인
                .andExpect(jsonPath("$.name").value(requestDTO.getName()))   // 이름 값이 일치하는지 확인
                .andDo(print()); // 요청/응답 전체 내용을 콘솔에 출력
    }

    @Test
    @DisplayName("회원가입 API 호출 실패 테스트 - 유효성 검증 실패(잘못된 이메일 형식)")
    void signup_api_fail_invalid_email() throws Exception {
        // given: 잘못된 이메일 형식을 가진 데이터가 주어졌을 때
        UserSignupRequestDTO requestDTO = UserSignupRequestDTO.builder()
                .email("invalid-email-format")
                .password("password1234")
                .name("통합테스트유저")
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // when & then: /api/users/signup POST 요청을 보냈을 때
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()) // 응답 상태 코드가 400 Bad Request인지 확인
                .andDo(print());
    }
}
