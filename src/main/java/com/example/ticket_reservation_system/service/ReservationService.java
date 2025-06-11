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

/**
 * 예매 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
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
        // 1. 사용자 정보 조회
        UserDomain user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + requestDTO.getUserId()));

        // 2. 좌석 정보 조회
        SeatDomain seat = seatRepository.findById(requestDTO.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다. ID: " + requestDTO.getSeatId()));

        // 3. 좌석의 예약 상태를 변경. 이미 예약된 경우 IllegalStateException 발생
        seat.reserve();

        // 4. 예매 정보 생성
        ReservationDomain reservation = ReservationDomain.builder()
                .user(user)
                .performance(seat.getPerformance())
                .seat(seat)
                .build();

        // 5. 예매 정보 저장 후 반환
        return reservationRepository.save(reservation);
    }
}