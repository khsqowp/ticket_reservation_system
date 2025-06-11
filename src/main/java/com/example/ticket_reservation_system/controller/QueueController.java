package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.security.UserDetailsImpl;
import com.example.ticket_reservation_system.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final WaitingQueueService waitingQueueService;

    // 대기열 진입 API
    @PostMapping("/enter/{performanceId}")
    public ResponseEntity<Map<String, Long>> enterQueue(
            @PathVariable Long performanceId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long rank = waitingQueueService.addUserToQueue(performanceId, userDetails.getUser().getId().toString());
        return ResponseEntity.ok(Map.of("rank", rank));
    }

    // 대기열 순번 확인 API
    @GetMapping("/check/{performanceId}")
    public ResponseEntity<Map<String, Long>> checkRank(
            @PathVariable Long performanceId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Long rank = waitingQueueService.getRank(performanceId, userDetails.getUser().getId().toString());
        return ResponseEntity.ok(Map.of("rank", rank));
    }

    // [관리자용] 대기열 사용자 입장 처리 API
    @Secured("ROLE_ADMIN")
    @PostMapping("/allow/{performanceId}")
    public ResponseEntity<String> allowUsers(
            @PathVariable Long performanceId,
            @RequestParam(defaultValue = "100") int count) {

        waitingQueueService.allowUsers(performanceId, count);
        return ResponseEntity.ok(count + "명의 사용자가 입장 허용되었습니다.");
    }
}