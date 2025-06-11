package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
import com.example.ticket_reservation_system.repository.PerformanceRepository;
import com.example.ticket_reservation_system.repository.ReservationRepository;
import com.example.ticket_reservation_system.repository.SeatRepository;
import com.example.ticket_reservation_system.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationServiceConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private UserDomain user;
    private SeatDomain seat;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 사용자, 공연, 좌석 데이터 생성
        user = userRepository.saveAndFlush(UserDomain.builder().email("concurrency@test.com").name("동시성테스터").password("1234").build());
        PerformanceDomain performance = performanceRepository.saveAndFlush(PerformanceDomain.builder().name("테스트공연").place("테스트장소").price(100).startTime(LocalDateTime.now().plusDays(1)).build());
        seat = seatRepository.saveAndFlush(new SeatDomain(performance, "A", "1"));
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("100명의 사용자가 동시에 같은 좌석을 예매하는 경우 테스트 (비관적 락)")
    void reserve_ticket_concurrently() throws InterruptedException {
        // given: 100개의 동시 요청 상황 설정
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 32개의 스레드 풀 생성
        CountDownLatch latch = new CountDownLatch(threadCount); // 모든 스레드가 작업을 완료할 때까지 대기하기 위한 장치
        AtomicInteger successCount = new AtomicInteger(); // 성공한 예매 수를 세기 위한 원자적 변수
        AtomicInteger failCount = new AtomicInteger();   // 실패한 예매 수를 세기 위한 원자적 변수

        // when: 100개의 스레드가 동시에 같은 좌석 예매를 요청
        for (int i = 0; i < threadCount; i++) {
            final int userId = i + 1; // 각기 다른 사용자인 것처럼 가정
            UserDomain testUser = userRepository.save(UserDomain.builder().email("user"+userId+"@test.com").name("유저"+userId).password("1234").build());

            executorService.submit(() -> {
                try {
                    ReservationRequestDTO requestDTO = new ReservationRequestDTO(testUser.getId(), seat.getId());
                    reservationService.reserveTicket(requestDTO);
                    successCount.incrementAndGet(); // 예매 성공 시 카운트 증가
                } catch (Exception e) {
                    // 예매 실패(예: 이미 예약된 좌석) 시 카운트 증가
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown(); // 작업 완료를 알림
                }
            });
        }

        latch.await(); // 모든 스레드의 작업이 끝날 때까지 대기
        executorService.shutdown();

        // then: 단 하나의 예매만 성공해야 한다.
        System.out.println("예매 성공 수: " + successCount.get());
        System.out.println("예매 실패 수: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        long reservationCount = reservationRepository.count();
        assertThat(reservationCount).isEqualTo(1);
    }
}
