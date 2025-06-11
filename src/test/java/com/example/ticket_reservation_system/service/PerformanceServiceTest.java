package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * PerformanceService에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class PerformanceServiceTest {

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    // ... (이전 테스트들은 생략)

    @Nested
    @DisplayName("공연 상세 조회 테스트")
    class FindPerformanceByIdTest {
        @Test
        @DisplayName("성공")
        void find_performance_by_id_success() {
            // given
            long performanceId = 1L;
            PerformanceDomain performance = PerformanceDomain.builder().name("테스트 공연").build();
            given(performanceRepository.findById(performanceId)).willReturn(Optional.of(performance));

            // when
            PerformanceDomain result = performanceService.findPerformanceById(performanceId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트 공연");
            verify(performanceRepository).findById(performanceId);
        }

        @Test
        @DisplayName("실패 - 해당 ID의 공연 없음")
        void find_performance_by_id_fail_not_found() {
            // given
            long nonExistentId = 99L;
            given(performanceRepository.findById(nonExistentId)).willReturn(Optional.empty());

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                performanceService.findPerformanceById(nonExistentId);
            }, "해당 ID의 공연을 찾을 수 없습니다: " + nonExistentId);

            verify(performanceRepository).findById(nonExistentId);
        }
    }
}