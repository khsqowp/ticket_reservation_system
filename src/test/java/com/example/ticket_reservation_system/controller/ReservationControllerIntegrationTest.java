package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.example.ticket_reservation_system.repository.ReservationRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private ReservationRepository reservationRepository;

    // ... (이전 테스트 클래스들 생략) ...

    @Nested
    @DisplayName("예매 취소 API 테스트")
    class CancelReservationApiTest {
        private ReservationDomain savedReservation;
        private SeatDomain reservedSeat;

        @BeforeEach
        void setUp() {
            UserDomain user = userRepository.save(UserDomain.builder().email("user@test.com").name("유저").password("1234").build());
            PerformanceDomain performance = performanceRepository.save(PerformanceDomain.builder().name("공연").place("장소").price(100).startTime(LocalDateTime.now().plusDays(1)).build());

            // 테스트를 위해 미리 좌석을 생성하고 예매까지 완료
            reservedSeat = new SeatDomain(performance, "R", "A1");
            reservedSeat.reserve();
            seatRepository.save(reservedSeat);
            savedReservation = reservationRepository.save(ReservationDomain.builder().user(user).performance(performance).seat(reservedSeat).build());
        }

        @Test
        @DisplayName("성공")
        void cancel_reservation_api_success() throws Exception {
            // given
            Long reservationId = savedReservation.getId();

            // when & then
            mockMvc.perform(delete("/api/reservations/" + reservationId))
                    .andExpect(status().isNoContent()) // 204 No Content
                    .andDo(print());

            // 예매 내역이 실제로 DB에서 삭제되었는지 확인
            boolean reservationExists = reservationRepository.existsById(reservationId);
            assertThat(reservationExists).isFalse();

            // 좌석의 isReserved 상태가 false로 롤백되었는지 확인
            SeatDomain seatAfterCancel = seatRepository.findById(reservedSeat.getId()).get();
            assertThat(seatAfterCancel.isReserved()).isFalse();
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 예매 ID")
        void cancel_reservation_api_fail_not_found() throws Exception {
            // given
            long nonExistentReservationId = 999L;

            // when & then
            mockMvc.perform(delete("/api/reservations/" + nonExistentReservationId))
                    .andExpect(status().isBadRequest()) // IllegalArgumentException은 400
                    .andExpect(jsonPath("$.error").value("예매 내역을 찾을 수 없습니다. ID: " + nonExistentReservationId))
                    .andDo(print());
        }
    }
}
