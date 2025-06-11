package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
import com.example.ticket_reservation_system.repository.ReservationRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
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
 * ReservationService에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SeatRepository seatRepository;

    @Test
    @DisplayName("티켓 예매 성공 테스트")
    void reserve_ticket_success() {
        // given
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(1L, 1L);
        UserDomain user = UserDomain.builder().name("테스트유저").build();
        PerformanceDomain performance = PerformanceDomain.builder().name("테스트공연").build();
        SeatDomain seat = new SeatDomain(performance, "R", "A1"); // isReserved=false

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(seatRepository.findById(1L)).willReturn(Optional.of(seat));
        given(reservationRepository.save(any(ReservationDomain.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ReservationDomain result = reservationService.reserveTicket(requestDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUser().getName()).isEqualTo("테스트유저");
        assertThat(result.getPerformance().getName()).isEqualTo("테스트공연");
        assertThat(result.getSeat().isReserved()).isTrue(); // 좌석 상태가 true로 변경되었는지 확인
        verify(reservationRepository).save(any(ReservationDomain.class));
    }

    @Test
    @DisplayName("티켓 예매 실패 테스트 - 이미 예약된 좌석")
    void reserve_ticket_fail_already_reserved() {
        // given
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(1L, 1L);
        UserDomain user = UserDomain.builder().build();
        PerformanceDomain performance = PerformanceDomain.builder().build();
        SeatDomain seat = new SeatDomain(performance, "R", "A1");
        seat.reserve(); // 좌석을 미리 예약 상태로 만듦

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(seatRepository.findById(1L)).willReturn(Optional.of(seat));

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            reservationService.reserveTicket(requestDTO);
        }, "이미 예약된 좌석입니다.");
    }
}