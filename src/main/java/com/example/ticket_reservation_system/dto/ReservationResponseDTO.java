package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.ReservationDomain;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 티켓 예매 성공 응답을 위한 DTO
 */
@Getter
public class ReservationResponseDTO {
    private final Long reservationId;
    private final String userName;
    private final String performanceName;
    private final String seatInfo; // 예: "R등급 A10"
    private final LocalDateTime reservedAt;

    private ReservationResponseDTO(Long reservationId, String userName, String performanceName, String seatInfo, LocalDateTime reservedAt) {
        this.reservationId = reservationId;
        this.userName = userName;
        this.performanceName = performanceName;
        this.seatInfo = seatInfo;
        this.reservedAt = reservedAt;
    }

    public static ReservationResponseDTO from(ReservationDomain reservation) {
        String seatInfo = String.format("%s등급 %s",
                reservation.getSeat().getGrade(),
                reservation.getSeat().getSeatNumber());

        return new ReservationResponseDTO(
                reservation.getId(),
                reservation.getUser().getName(),
                reservation.getPerformance().getName(),
                seatInfo,
                reservation.getReservedAt()
        );
    }
}