package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 공연 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    /**
     * 새로운 공연 정보를 등록합니다.
     * @param requestDTO 등록할 공연 정보
     * @return 저장된 PerformanceDomain 객체
     */
    @Transactional
    public PerformanceDomain registerPerformance(PerformanceRequestDTO requestDTO) {
        PerformanceDomain performance = requestDTO.toEntity();
        return performanceRepository.save(performance);
    }
}