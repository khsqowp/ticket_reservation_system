package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    @DisplayName("티켓 예매 API 테스트")
    class CreateReservationApiTest {
        private UserDomain user;
        private SeatDomain availableSeat;
        private SeatDomain reservedSeat;

        @BeforeEach
        void setUp() {
            user = userRepository.save(UserDomain.builder().email("user@test.com").name("유저").password("1234").build());
            PerformanceDomain performance = performanceRepository.save(PerformanceDomain.builder().name("공연").place("장소").price(100).startTime(LocalDateTime.now().plusDays(1)).build());
            availableSeat = seatRepository.save(new SeatDomain(performance, "R", "A1"));
            reservedSeat = new SeatDomain(performance, "R", "A2");
            reservedSeat.reserve();
            seatRepository.save(reservedSeat);
        }

        @Test
        @DisplayName("성공")
        void createReservation_api_success() throws Exception {
            ReservationRequestDTO requestDTO = new ReservationRequestDTO(user.getId(), availableSeat.getId());

            mockMvc.perform(post("/api/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.userName").value(user.getName()))
                    .andDo(print());

            SeatDomain seatAfterReservation = seatRepository.findById(availableSeat.getId()).get();
            assertThat(seatAfterReservation.isReserved()).isTrue();
        }

        @Test
        @DisplayName("실패 - 이미 예약된 좌석")
        void createReservation_api_fail_already_reserved() throws Exception {
            ReservationRequestDTO requestDTO = new ReservationRequestDTO(user.getId(), reservedSeat.getId());

            mockMvc.perform(post("/api/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("이미 예약된 좌석입니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("내 예매 내역 조회 API 테스트")
    class GetMyReservationsApiTest {
        private UserDomain user1;
        private UserDomain user2;

        @BeforeEach
        void setUp() {
            user1 = userRepository.save(UserDomain.builder().email("user1@test.com").name("유저1").password("1234").build());
            user2 = userRepository.save(UserDomain.builder().email("user2@test.com").name("유저2").password("1234").build());
            PerformanceDomain performance = performanceRepository.save(PerformanceDomain.builder().name("공연").place("장소").price(100).startTime(LocalDateTime.now().plusDays(1)).build());
            SeatDomain seat1 = seatRepository.save(new SeatDomain(performance, "R", "A1"));
            SeatDomain seat2 = seatRepository.save(new SeatDomain(performance, "R", "A2"));

            reservationRepository.save(ReservationDomain.builder().user(user1).performance(performance).seat(seat1).build());
            reservationRepository.save(ReservationDomain.builder().user(user1).performance(performance).seat(seat2).build());
        }

        @Test
        @DisplayName("성공 - 예매 내역 2건 조회")
        void get_my_reservations_success() throws Exception {
            mockMvc.perform(get("/api/reservations/my-reservations/" + user1.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].userName").value("유저1"))
                    .andDo(print());
        }

        @Test
        @DisplayName("성공 - 예매 내역 0건 조회")
        void get_my_reservations_success_empty() throws Exception {
            mockMvc.perform(get("/api/reservations/my-reservations/" + user2.getId())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자 ID")
        void get_my_reservations_fail_user_not_found() throws Exception {
            long nonExistentUserId = 999L;

            mockMvc.perform(get("/api/reservations/my-reservations/" + nonExistentUserId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("사용자를 찾을 수 없습니다. ID: " + nonExistentUserId))
                    .andDo(print());
        }
    }
}
