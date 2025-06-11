package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.repository.ReservationRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import com.example.ticket_reservation_system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // ... (이전 테스트들은 생략) ...

    @Nested
    @DisplayName("예매 취소 테스트")
    class CancelReservationTest {

        @Test
        @DisplayName("성공")
        void cancel_reservation_success() {
            // given
            long reservationId = 1L;
            PerformanceDomain performance = PerformanceDomain.builder().build();
            SeatDomain seat = new SeatDomain(performance, "R", "A1");
            seat.reserve(); // 좌석이 예약된 상태
            ReservationDomain reservation = ReservationDomain.builder().seat(seat).build();

            given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

            // when
            reservationService.cancelReservation(reservationId);

            // then
            assertThat(seat.isReserved()).isFalse(); // 좌석의 예약 상태가 false로 변경되었는지 확인
            verify(reservationRepository).findById(reservationId);
            verify(reservationRepository).delete(reservation); // delete 메소드가 호출되었는지 확인
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 예매 ID")
        void cancel_reservation_fail_not_found() {
            // given
            long nonExistentReservationId = 99L;
            given(reservationRepository.findById(nonExistentReservationId)).willReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                reservationService.cancelReservation(nonExistentReservationId);
            });
        }
    }
}