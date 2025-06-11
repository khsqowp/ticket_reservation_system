package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
import com.example.ticket_reservation_system.repository.ReservationRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import com.example.ticket_reservation_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 예매 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final WaitingQueueService waitingQueueService; // 대기열 서비스 주입

    @Transactional
    public ReservationDomain reserveTicket(ReservationRequestDTO requestDTO) {
        // 1. 사용자 정보 조회
        UserDomain user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + requestDTO.getUserId()));

        // 2. 좌석 정보 조회 시 비관적 락을 사용합니다.
        SeatDomain seat = seatRepository.findByIdWithPessimisticLock(requestDTO.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다. ID: " + requestDTO.getSeatId()));

        // 3. [추가] 대기열에서 허용된 사용자인지 확인
        Long performanceId = seat.getPerformance().getId();
        if (!waitingQueueService.isAllowed(performanceId, user.getId().toString())) {
            throw new IllegalStateException("예매 가능 순서가 아닙니다. 대기열을 확인해주세요.");
        }

        // 4. 좌석의 예약 상태를 변경. 이미 예약된 경우 IllegalStateException 발생
        seat.reserve();

        // 5. 예매 정보 생성
        ReservationDomain reservation = ReservationDomain.builder()
                .user(user)
                .performance(seat.getPerformance())
                .seat(seat)
                .build();

        // 6. 예매 완료 후, 허용 목록에서 최종적으로 제거
        waitingQueueService.markAsProcessed(performanceId, user.getId().toString());

        // 7. 예매 정보 저장 후 반환
        return reservationRepository.save(reservation);
    }

    public List<ReservationDomain> findMyReservations(Long userId) {
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        return reservationRepository.findAllByUser(user);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        ReservationDomain reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역을 찾을 수 없습니다. ID: " + reservationId));

        SeatDomain seat = reservation.getSeat();
        seat.cancel();

        reservationRepository.delete(reservation);
    }
}