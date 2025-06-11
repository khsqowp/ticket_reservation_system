package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PerformanceController에 대한 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PerformanceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // LocalDateTime 직렬화/역직렬화를 위한 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
    }


    @Test
    @DisplayName("공연 정보 등록 API 호출 성공 테스트")
    void register_performance_api_success() throws Exception {
        // given: 이러한 데이터가 주어졌을 때
        PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                .name("싸이 흠뻑쇼")
                .place("잠실종합운동장")
                .price(132000)
                .startTime(LocalDateTime.of(2025, 8, 15, 18, 30))
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // when & then: /api/performances POST 요청을 보냈을 때
        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(requestDTO.getName()))
                .andExpect(jsonPath("$.place").value(requestDTO.getPlace()))
                .andExpect(jsonPath("$.price").value(requestDTO.getPrice()))
                .andDo(print());
    }

    @Test
    @DisplayName("공연 정보 등록 API 호출 실패 테스트 - 유효성 검증 실패(공연명이 없음)")
    void register_performance_api_fail_blank_name() throws Exception {
        // given: 공연명이 비어있는 데이터가 주어졌을 때
        PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                .name("") // 유효성 검증 실패 조건
                .place("홍대 라이브클럽")
                .price(88000)
                .startTime(LocalDateTime.of(2025, 9, 20, 19, 0))
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // when & then: /api/performances POST 요청을 보냈을 때
        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()) // 400 Bad Request 응답을 기대
                .andDo(print());
    }

    @Test
    @DisplayName("공연 정보 등록 API 호출 실패 테스트 - 유효성 검증 실패(과거 시간)")
    void register_performance_api_fail_past_time() throws Exception {
        // given: 공연 시작 시간이 과거인 데이터가 주어졌을 때
        PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                .name("클래식 공연")
                .place("예술의전당")
                .price(99000)
                .startTime(LocalDateTime.now().minusDays(1)) // 유효성 검증 실패 조건 (@Future)
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        // when & then: /api/performances POST 요청을 보냈을 때
        mockMvc.perform(post("/api/performances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest()) // 400 Bad Request 응답을 기대
                .andDo(print());
    }
}