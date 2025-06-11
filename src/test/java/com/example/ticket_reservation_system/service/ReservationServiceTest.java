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

    // ... 이전 테스트 생략 ...

    @Nested
    @DisplayName("내 예매 내역 조회 테스트")
    class FindMyReservationsTest {
        @Test
        @DisplayName("성공")
        void find_my_reservations_success() {
            // given
            long userId = 1L;
            UserDomain user = UserDomain.builder().build();
            ReservationDomain reservation1 = ReservationDomain.builder().build();
            ReservationDomain reservation2 = ReservationDomain.builder().build();
            List<ReservationDomain> reservations = List.of(reservation1, reservation2);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));
            given(reservationRepository.findAllByUser(user)).willReturn(reservations);

            // when
            List<ReservationDomain> result = reservationService.findMyReservations(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.size()).isEqualTo(2);
            verify(userRepository).findById(userId);
            verify(reservationRepository).findAllByUser(user);
        }

        @Test
        @DisplayName("실패 - 사용자를 찾을 수 없음")
        void find_my_reservations_fail_user_not_found() {
            // given
            long nonExistentUserId = 99L;
            given(userRepository.findById(nonExistentUserId)).willReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                reservationService.findMyReservations(nonExistentUserId);
            });
        }
    }
}