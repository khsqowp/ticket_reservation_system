package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.dto.SeatRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * SeatService에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Test
    @DisplayName("좌석 등록 성공 테스트")
    void register_seat_success() {
        // given: 이러한 데이터가 주어졌을 때
        long performanceId = 1L;
        SeatRequestDTO requestDTO = SeatRequestDTO.builder()
                .performanceId(performanceId)
                .grade("VIP")
                .seatNumber("A10")
                .build();

        PerformanceDomain performance = PerformanceDomain.builder()
                .name("테스트 공연")
                .place("테스트 장소")
                .price(10000)
                .startTime(LocalDateTime.now().plusDays(1))
                .build();

        SeatDomain seatToSave = SeatDomain.builder()
                .performance(performance)
                .grade(requestDTO.getGrade())
                .seatNumber(requestDTO.getSeatNumber())
                .build();

        given(performanceRepository.findById(performanceId)).willReturn(Optional.of(performance));
        given(seatRepository.save(any(SeatDomain.class))).willReturn(seatToSave);

        // when: 이 메소드를 실행하면
        SeatDomain result = seatService.registerSeat(requestDTO);

        // then: 이러한 결과가 나와야 한다
        assertThat(result).isNotNull();
        assertThat(result.getPerformance().getName()).isEqualTo("테스트 공연");
        assertThat(result.getGrade()).isEqualTo("VIP");
        assertThat(result.getSeatNumber()).isEqualTo("A10");

        verify(performanceRepository).findById(performanceId);
        verify(seatRepository).save(any(SeatDomain.class));
    }

    @Test
    @DisplayName("좌석 등록 실패 테스트 - 존재하지 않는 공연 ID")
    void register_seat_fail_performance_not_found() {
        // given: 존재하지 않는 공연 ID가 주어졌을 때
        long nonExistentPerformanceId = 99L;
        SeatRequestDTO requestDTO = SeatRequestDTO.builder()
                .performanceId(nonExistentPerformanceId)
                .grade("R")
                .seatNumber("B5")
                .build();

        given(performanceRepository.findById(nonExistentPerformanceId)).willReturn(Optional.empty());

        // when & then: 이 메소드를 실행하면 IllegalArgumentException이 발생해야 한다
        assertThrows(IllegalArgumentException.class, () -> {
            seatService.registerSeat(requestDTO);
        });

        verify(performanceRepository).findById(nonExistentPerformanceId);
    }
}