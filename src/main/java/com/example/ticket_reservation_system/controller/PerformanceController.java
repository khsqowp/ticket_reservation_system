package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.dto.PerformanceResponseDTO;
import com.example.ticket_reservation_system.service.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performances")
public class PerformanceController {

    private final PerformanceService performanceService;

    @Secured(UserRoleEnum.Authority.ADMIN) // 관리자만 접근 가능
    @PostMapping
    public ResponseEntity<PerformanceResponseDTO> register(@Valid @RequestBody PerformanceRequestDTO requestDTO) {
        PerformanceDomain savedPerformance = performanceService.registerPerformance(requestDTO);
        PerformanceResponseDTO responseDTO = PerformanceResponseDTO.from(savedPerformance);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<PerformanceResponseDTO>> getAllPerformances() {
        List<PerformanceDomain> performances = performanceService.findAllPerformances();
        List<PerformanceResponseDTO> responseDTOs = performances.stream()
                .map(PerformanceResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{performanceId}")
    public ResponseEntity<PerformanceResponseDTO> getPerformanceById(@PathVariable Long performanceId) {
        PerformanceDomain performance = performanceService.findPerformanceById(performanceId);
        PerformanceResponseDTO responseDTO = PerformanceResponseDTO.from(performance);
        return ResponseEntity.ok(responseDTO);
    }
}
