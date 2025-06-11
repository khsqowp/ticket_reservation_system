package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공연 정보 응답을 위한 DTO
 */
@Getter
public class PerformanceResponseDTO {
    private final Long id;
    private final String name;
    private final String place;
    private final int price;
    private final LocalDateTime startTime;
    private final LocalDateTime createdAt;

    private PerformanceResponseDTO(Long id, String name, String place, int price, LocalDateTime startTime, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.price = price;
        this.startTime = startTime;
        this.createdAt = createdAt;
    }

    public static PerformanceResponseDTO from(PerformanceDomain performanceDomain) {
        return new PerformanceResponseDTO(
                performanceDomain.getId(),
                performanceDomain.getName(),
                performanceDomain.getPlace(),
                performanceDomain.getPrice(),
                performanceDomain.getStartTime(),
                performanceDomain.getCreatedAt()
        );
    }
}