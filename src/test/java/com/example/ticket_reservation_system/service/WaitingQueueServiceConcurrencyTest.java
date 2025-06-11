package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
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
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WaitingQueueServiceConcurrencyTest {

    @Autowired
    private WaitingQueueService waitingQueueService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private PerformanceDomain performance;
    private List<UserDomain> users;
    private SeatDomain seat;

    private final String waitingQueueKey = "queue:performance:1";
    private final String allowedUserSetKey = "allowed:performance:1";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        performance = performanceRepository.saveAndFlush(
                PerformanceDomain.builder()
                        .name("대규모 동시성 테스트 공연")
                        .place("테스트 월드")
                        .price(150000)
                        .startTime(LocalDateTime.now().plusDays(1))
                        .build()
        );

        users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            users.add(userRepository.save(
                    UserDomain.builder()
                            .email("user" + i + "@test.com")
                            .name("유저" + i)
                            .password("password")
                            .role(UserRoleEnum.USER)
                            .build())
            );
        }

        seat = seatRepository.saveAndFlush(
                new SeatDomain(performance, "VIP", "V1")
        );
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        reservationRepository.deleteAll();
        seatRepository.deleteAll();
        performanceRepository.deleteAll();
        userRepository.deleteAll();
        // Redis 데이터 정리
        redisTemplate.delete(waitingQueueKey);
        redisTemplate.delete(allowedUserSetKey);
    }

    @Test
    @DisplayName("1000명의 사용자가 동시에 대기열에 진입하고, 100명씩 입장시켜 예매하는 시나리오 테스트")
    void enter_allow_reserve_concurrently() throws InterruptedException {
        // given: 1000명의 동시 접속
        int totalUsers = 1000;
        int allowedCount = 100; // 한 번에 100명씩 입장
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        // when: 1. 1000명의 사용자가 동시에 대기열 진입을 시도
        for (UserDomain user : users) {
            executorService.submit(() -> {
                try {
                    waitingQueueService.addUserToQueue(performance.getId(), user.getId().toString());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then: 1. 대기열에 1000명이 모두 등록되었는지 확인
        Long queueSize = redisTemplate.opsForZSet().size(waitingQueueKey);
        assertThat(queueSize).isEqualTo(totalUsers);
        System.out.println("1단계: 1000명 대기열 진입 완료, 현재 대기 인원: " + queueSize);

        // when: 2. 관리자가 100명을 입장시킴
        waitingQueueService.allowUsers(performance.getId(), allowedCount);

        // then: 2. 대기열과 허용 목록 상태 확인
        Long queueSizeAfterAllow = redisTemplate.opsForZSet().size(waitingQueueKey);
        Long allowedSetSize = redisTemplate.opsForSet().size(allowedUserSetKey);
        assertThat(queueSizeAfterAllow).isEqualTo(totalUsers - allowedCount);
        assertThat(allowedSetSize).isEqualTo(allowedCount);
        System.out.println("2단계: 100명 입장 완료, 현재 대기 인원: " + queueSizeAfterAllow + ", 입장 허용 인원: " + allowedSetSize);

        // when: 3. 입장 허용된 100명이 동시에 하나의 좌석 예매를 시도
        CountDownLatch reservationLatch = new CountDownLatch(allowedCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        List<UserDomain> allowedUsers = users.subList(0, allowedCount);

        for (UserDomain allowedUser : allowedUsers) {
            executorService.submit(() -> {
                try {
                    // isAllowed 검사를 통과한 사용자만 예매 시도
                    if (waitingQueueService.isAllowed(performance.getId(), allowedUser.getId().toString())) {
                        ReservationRequestDTO requestDTO = new ReservationRequestDTO(allowedUser.getId(), seat.getId());
                        reservationService.reserveTicket(requestDTO);
                        successCount.incrementAndGet();
                    } else {
                        // isAllowed에서 걸러지는 경우는 거의 없지만, 방어적으로 카운트
                        failCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    reservationLatch.countDown();
                }
            });
        }
        reservationLatch.await();
        executorService.shutdown();

        // then: 3. 단 1명만 예매에 성공하고, 나머지는 실패해야 함
        System.out.println("3단계: 100명 예매 시도 결과");
        System.out.println(" -> 예매 성공: " + successCount.get());
        System.out.println(" -> 예매 실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(allowedCount - 1);

        // 4. 최종 데이터 상태 확인
        long dbReservationCount = reservationRepository.count();
        Long allowedSetSizeAfterReservation = redisTemplate.opsForSet().size(allowedUserSetKey);

        System.out.println("4단계: 최종 상태 확인");
        System.out.println(" -> DB에 저장된 예매 건수: " + dbReservationCount);
        System.out.println(" -> 예매 후 남은 입장 허용 인원: " + allowedSetSizeAfterReservation);

        assertThat(dbReservationCount).isEqualTo(1); // DB에도 예매는 1건만 기록
        assertThat(allowedSetSizeAfterReservation).isEqualTo(allowedCount - 1); // 예매에 성공한 1명은 허용 목록에서 제거됨
    }
}

