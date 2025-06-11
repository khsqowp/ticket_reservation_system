package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    private PerformanceRepository performanceRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ... (이전 테스트들은 생략)

    @Nested
    @DisplayName("공연 상세 조회 API 테스트")
    class GetPerformanceByIdApiTest {

        private PerformanceDomain savedPerformance;

        @BeforeEach
        void setUp() {
            savedPerformance = performanceRepository.save(PerformanceDomain.builder()
                    .name("상세 조회 테스트 공연")
                    .place("테스트 장소")
                    .price(50000)
                    .startTime(LocalDateTime.now().plusDays(5))
                    .build());
        }

        @Test
        @DisplayName("성공")
        void get_performance_by_id_api_success() throws Exception {
            // when & then
            mockMvc.perform(get("/api/performances/" + savedPerformance.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedPerformance.getId()))
                    .andExpect(jsonPath("$.name").value("상세 조회 테스트 공연"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID")
        void get_performance_by_id_api_fail_not_found() throws Exception {
            // given
            long nonExistentId = 999L;

            // when & then
            mockMvc.perform(get("/api/performances/" + nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("해당 ID의 공연을 찾을 수 없습니다: " + nonExistentId))
                    .andDo(print());
        }
    }
}
