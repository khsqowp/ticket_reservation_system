package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import com.example.ticket_reservation_system.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ReservationController에 대한 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PerformanceRepository performanceRepository;
    @Autowired
    private SeatRepository seatRepository;

    private UserDomain user;
    private PerformanceDomain performance;
    private SeatDomain availableSeat;
    private SeatDomain reservedSeat;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserDomain.builder().email("user@test.com").name("유저").password("1234").build());
        performance = performanceRepository.save(PerformanceDomain.builder().name("공연").place("장소").price(100).startTime(LocalDateTime.now().plusDays(1)).build());
        availableSeat = seatRepository.save(new SeatDomain(performance, "R", "A1"));

        reservedSeat = new SeatDomain(performance, "R", "A2");
        reservedSeat.reserve();
        seatRepository.save(reservedSeat);
    }

    @Test
    @DisplayName("티켓 예매 API 호출 성공")
    void createReservation_api_success() throws Exception {
        // given
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(user.getId(), availableSeat.getId());

        // when & then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId").exists())
                .andExpect(jsonPath("$.userName").value(user.getName()))
                .andExpect(jsonPath("$.performanceName").value(performance.getName()))
                .andExpect(jsonPath("$.seatInfo").value("R등급 A1"))
                .andDo(print());

        // 예매 후 좌석 상태가 실제로 변경되었는지 DB에서 확인
        SeatDomain seatAfterReservation = seatRepository.findById(availableSeat.getId()).get();
        assertThat(seatAfterReservation.isReserved()).isTrue();
    }

    @Test
    @DisplayName("티켓 예매 API 호출 실패 - 이미 예약된 좌석")
    void createReservation_api_fail_already_reserved() throws Exception {
        // given
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(user.getId(), reservedSeat.getId());

        // when & then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict()) // 409 Conflict
                .andExpect(jsonPath("$.error").value("이미 예약된 좌석입니다."))
                .andDo(print());
    }
}