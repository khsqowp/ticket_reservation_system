package com.example.ticket_reservation_system.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 공연 정보를 담는 도메인 클래스 (Entity)
 * 데이터베이스의 'performances' 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "performances")
public class PerformanceDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String place;

    @Column(nullable = false)
    private int price;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PerformanceDomain(String name, String place, int price, LocalDateTime startTime) {
        this.name = name;
        this.place = place;
        this.price = price;
        this.startTime = startTime;
    }
}