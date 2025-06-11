package com.example.ticket_reservation_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 정보 등록 요청을 위한 DTO
 */
@Getter
@NoArgsConstructor
public class SeatRequestDTO {

    @NotNull(message = "공연 ID는 필수 입력 항목입니다.")
    private Long performanceId;

    @NotBlank(message = "좌석 등급은 필수 입력 항목입니다.")
    private String grade;

    @NotBlank(message = "좌석 번호는 필수 입력 항목입니다.")
    private String seatNumber;

    @Builder
    public SeatRequestDTO(Long performanceId, String grade, String seatNumber) {
        this.performanceId = performanceId;
        this.grade = grade;
        this.seatNumber = seatNumber;
    }
}