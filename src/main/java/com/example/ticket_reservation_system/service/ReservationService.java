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

    /**
     * 티켓을 예매합니다.
     * @param requestDTO 예매 요청 정보
     * @return 생성된 ReservationDomain 객체
     */
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

    /**
     * 특정 사용자의 모든 예매 내역을 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 예매 내역 리스트
     */
    public List<ReservationDomain> findMyReservations(Long userId) {
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        return reservationRepository.findAllByUser(user);
    }
}