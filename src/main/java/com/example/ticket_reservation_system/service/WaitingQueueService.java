package com.example.ticket_reservation_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WaitingQueueService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_QUEUE_KEY_PREFIX = "queue:performance:";
    private static final String ALLOWED_USER_SET_KEY_PREFIX = "allowed:performance:";

    // 대기열에 사용자 추가
    public Long addUserToQueue(Long performanceId, String userId) {
        String key = WAITING_QUEUE_KEY_PREFIX + performanceId;
        long now = Instant.now().toEpochMilli(); // 현재 시간을 점수(score)로 사용하여 선착순 정렬
        redisTemplate.opsForZSet().add(key, userId, now);
        // ZSet(Sorted Set)은 score 기준으로 자동 정렬됩니다.
        return redisTemplate.opsForZSet().rank(key, userId); // 현재 나의 대기 순번 반환 (0부터 시작)
    }

    // 대기열에서 나의 순번 확인
    public Long getRank(Long performanceId, String userId) {
        String key = WAITING_QUEUE_KEY_PREFIX + performanceId;
        return redisTemplate.opsForZSet().rank(key, userId);
    }

    // 대기열에서 다음 사용자들을 예매 가능 상태로 전환
    public void allowUsers(Long performanceId, int count) {
        String waitingKey = WAITING_QUEUE_KEY_PREFIX + performanceId;
        String allowedKey = ALLOWED_USER_SET_KEY_PREFIX + performanceId;

        // 대기열에서 가장 앞선 사용자(count 만큼)를 가져옴
        Set<String> waitingUsers = redisTemplate.opsForZSet().range(waitingKey, 0, count - 1);

        if (waitingUsers != null && !waitingUsers.isEmpty()) {
            // 허용된 사용자 Set에 추가
            redisTemplate.opsForSet().add(allowedKey, waitingUsers.toArray(new String[0]));
            // 기존 대기열에서 제거
            redisTemplate.opsForZSet().removeRange(waitingKey, 0, waitingUsers.size() - 1);
        }
    }

    // 사용자가 예매 가능한 상태인지 확인
    public boolean isAllowed(Long performanceId, String userId) {
        String key = ALLOWED_USER_SET_KEY_PREFIX + performanceId;
        // Set에 해당 사용자가 있는지 확인하고, 있으면 삭제 (1회용 입장권처럼 사용)
        return redisTemplate.opsForSet().isMember(key, userId);
    }

    // 예매 완료 후 허용 목록에서 제거
    public void markAsProcessed(Long performanceId, String userId) {
        String key = ALLOWED_USER_SET_KEY_PREFIX + performanceId;
        redisTemplate.opsForSet().remove(key, userId);
    }
}