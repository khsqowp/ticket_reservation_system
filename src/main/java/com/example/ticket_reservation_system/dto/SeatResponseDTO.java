package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.SeatDomain;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * 좌석 정보 응답을 위한 DTO
 */
@Getter
public class SeatResponseDTO {
    private final Long id;
    private final Long performanceId;
    private final String grade;
    private final String seatNumber;

    @JsonProperty("isReserved") // [수정] JSON 필드명을 "isReserved"로 강제합니다.
    private final boolean isReserved;

    private SeatResponseDTO(Long id, Long performanceId, String grade, String seatNumber, boolean isReserved) {
        this.id = id;
        this.performanceId = performanceId;
        this.grade = grade;
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
    }

    public static SeatResponseDTO from(SeatDomain seat) {
        return new SeatResponseDTO(
                seat.getId(),
                seat.getPerformance().getId(),
                seat.getGrade(),
                seat.getSeatNumber(),
                seat.isReserved()
        );
    }
}