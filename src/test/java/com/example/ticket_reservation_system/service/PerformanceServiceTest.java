package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.dto.PerformanceRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    @DisplayName("공연 정보 등록 성공 테스트")
    void register_performance_success() {
        // given: 이러한 데이터가 주어졌을 때
        LocalDateTime startTime = LocalDateTime.now().plusDays(10);
        PerformanceRequestDTO requestDTO = PerformanceRequestDTO.builder()
                .name("뮤지컬 <레미제라블>")
                .place("블루스퀘어 신한카드홀")
                .price(150000)
                .startTime(startTime)
                .build();

        PerformanceDomain performanceToSave = requestDTO.toEntity();
        PerformanceDomain savedPerformance = PerformanceDomain.builder()
                .name("뮤지컬 <레미제라블>")
                .place("블루스퀘어 신한카드홀")
                .price(150000)
                .startTime(startTime)
                .build();

        // performanceRepository.save()가 호출되면 savedPerformance를 반환하도록 설정
        given(performanceRepository.save(any(PerformanceDomain.class))).willReturn(savedPerformance);

        // when: 이 메소드를 실행하면
        PerformanceDomain result = performanceService.registerPerformance(requestDTO);

        // then: 이러한 결과가 나와야 한다
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(requestDTO.getName());
        assertThat(result.getPlace()).isEqualTo(requestDTO.getPlace());
        assertThat(result.getPrice()).isEqualTo(requestDTO.getPrice());

        // performanceRepository.save 메소드가 한 번 호출되었는지 검증
        verify(performanceRepository).save(any(PerformanceDomain.class));
    }
}