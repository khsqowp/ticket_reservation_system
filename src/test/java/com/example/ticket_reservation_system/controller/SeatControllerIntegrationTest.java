package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.SeatRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * SeatController에 대한 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SeatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PerformanceRepository performanceRepository;

    private PerformanceDomain savedPerformance;

    @BeforeEach
    void setUp() {
        // 모든 테스트 실행 전에 공연 정보를 미리 저장
        savedPerformance = performanceRepository.save(PerformanceDomain.builder()
                .name("통합 테스트용 공연")
                .place("테스트 공연장")
                .price(120000)
                .startTime(LocalDateTime.now().plusMonths(1))
                .build());
    }

    @Test
    @DisplayName("좌석 등록 API 호출 성공 테스트")
    void register_seat_api_success() throws Exception {
        // given: 이러한 데이터가 주어졌을 때
        SeatRequestDTO requestDTO = SeatRequestDTO.builder()
                .performanceId(savedPerformance.getId())
                .grade("R")
                .seatNumber("C7")
                .build();

        // when & then: /api/seats POST 요청을 보냈을 때
        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.performanceId").value(savedPerformance.getId()))
                .andExpect(jsonPath("$.grade").value("R"))
                .andExpect(jsonPath("$.seatNumber").value("C7"))
                .andExpect(jsonPath("$.isReserved").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("좌석 등록 API 호출 실패 테스트 - 존재하지 않는 공연 ID")
    void register_seat_api_fail_performance_not_found() throws Exception {
        // given: 존재하지 않는 공연 ID가 주어졌을 때
        long nonExistentPerformanceId = 999L;
        SeatRequestDTO requestDTO = SeatRequestDTO.builder()
                .performanceId(nonExistentPerformanceId)
                .grade("S")
                .seatNumber("D11")
                .build();

        // when & then: /api/seats POST 요청을 보냈을 때
        mockMvc.perform(post("/api/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("해당 ID의 공연을 찾을 수 없습니다: " + nonExistentPerformanceId))
                .andDo(print());
    }
}