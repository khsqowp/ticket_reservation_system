package com.example.ticket_reservation_system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 티켓 예매 요청을 위한 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor // [수정] 모든 필드를 인자로 받는 생성자를 추가합니다.
public class ReservationRequestDTO {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;

    @NotNull(message = "좌석 ID는 필수입니다.")
    private Long seatId;
}