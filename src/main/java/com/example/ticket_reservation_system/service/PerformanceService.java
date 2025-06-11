package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * 등록된 모든 공연 목록을 조회합니다.
     * @return 모든 PerformanceDomain 객체 리스트
     */
    @Transactional(readOnly = true)
    public List<PerformanceDomain> findAllPerformances() {
        return performanceRepository.findAll();
    }

    /**
     * ID로 특정 공연 정보를 조회합니다.
     * @param performanceId 조회할 공연의 ID
     * @return 조회된 PerformanceDomain 객체
     */
    @Transactional(readOnly = true)
    public PerformanceDomain findPerformanceById(Long performanceId) {
        return performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 공연을 찾을 수 없습니다: " + performanceId));
    }
}