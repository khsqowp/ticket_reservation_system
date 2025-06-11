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

    @Transactional
    public ReservationDomain reserveTicket(ReservationRequestDTO requestDTO) {
        UserDomain user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + requestDTO.getUserId()));

        SeatDomain seat = seatRepository.findByIdWithPessimisticLock(requestDTO.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다. ID: " + requestDTO.getSeatId()));

        seat.reserve();

        ReservationDomain reservation = ReservationDomain.builder()
                .user(user)
                .performance(seat.getPerformance())
                .seat(seat)
                .build();

        return reservationRepository.save(reservation);
    }

    public List<ReservationDomain> findMyReservations(Long userId) {
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        return reservationRepository.findAllByUser(user);
    }

    /**
     * 예매를 취소합니다.
     * @param reservationId 취소할 예매의 ID
     */
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 1. 취소할 예매 내역을 조회합니다. 없으면 예외를 발생시킵니다.
        ReservationDomain reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예매 내역을 찾을 수 없습니다. ID: " + reservationId));

        // 2. 예매된 좌석의 상태를 '예약 가능'으로 되돌립니다.
        SeatDomain seat = reservation.getSeat();
        seat.cancel();

        // 3. 예매 내역을 삭제합니다.
        reservationRepository.delete(reservation);
    }
}