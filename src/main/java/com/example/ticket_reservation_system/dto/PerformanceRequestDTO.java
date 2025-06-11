package com.example.ticket_reservation_system.dto;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 공연 정보 등록 요청을 위한 DTO
 */
@Getter
@NoArgsConstructor
public class PerformanceRequestDTO {

    @NotBlank(message = "공연명은 필수 입력 항목입니다.")
    private String name;

    @NotBlank(message = "공연 장소는 필수 입력 항목입니다.")
    private String place;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @NotNull(message = "공연 시작 시간은 필수 입력 항목입니다.")
    @Future(message = "공연 시작 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime startTime;

    @Builder
    public PerformanceRequestDTO(String name, String place, int price, LocalDateTime startTime) {
        this.name = name;
        this.place = place;
        this.price = price;
        this.startTime = startTime;
    }

    public PerformanceDomain toEntity() {
        return PerformanceDomain.builder()
                .name(this.name)
                .place(this.place)
                .price(this.price)
                .startTime(this.startTime)
                .build();
    }
}