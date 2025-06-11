package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.dto.PerformanceResponseDTO;
import com.example.ticket_reservation_system.service.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 공연과 관련된 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class PerformanceController {

    private final PerformanceService performanceService;

    /**
     * 새로운 공연 정보를 등록하는 API
     * @param requestDTO 등록할 공연 정보 DTO
     * @return 생성된 공연 정보와 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<PerformanceResponseDTO> register(@Valid @RequestBody PerformanceRequestDTO requestDTO) {
        PerformanceDomain savedPerformance = performanceService.registerPerformance(requestDTO);
        PerformanceResponseDTO responseDTO = PerformanceResponseDTO.from(savedPerformance);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}