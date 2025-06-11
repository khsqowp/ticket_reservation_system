package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.dto.SeatRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 좌석 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;

    /**
     * 특정 공연에 대한 새로운 좌석을 등록합니다.
     * @param requestDTO 등록할 좌석 정보
     * @return 저장된 SeatDomain 객체
     */
    @Transactional
    public SeatDomain registerSeat(SeatRequestDTO requestDTO) {
        PerformanceDomain performance = performanceRepository.findById(requestDTO.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 공연을 찾을 수 없습니다: " + requestDTO.getPerformanceId()));

        SeatDomain seat = SeatDomain.builder()
                .performance(performance)
                .grade(requestDTO.getGrade())
                .seatNumber(requestDTO.getSeatNumber())
                .build();

        return seatRepository.save(seat);
    }
}