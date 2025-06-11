package com.example.ticket_reservation_system.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 좌석 정보를 담는 도메인 클래스 (Entity)
 * 데이터베이스의 'seats' 테이블과 매핑됩니다.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seats")
public class SeatDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private PerformanceDomain performance;

    @Column(nullable = false, length = 10)
    private String grade; // 예: "VIP", "R", "S", "A"

    @Column(name = "seat_number", nullable = false, length = 10)
    private String seatNumber; // 예: "A1", "B5"

    @Column(name = "is_reserved", nullable = false)
    private boolean isReserved = false; // 기본값은 '예약되지 않음'

    @Builder
    public SeatDomain(PerformanceDomain performance, String grade, String seatNumber) {
        this.performance = performance;
        this.grade = grade;
        this.seatNumber = seatNumber;
    }

    /**
     * 좌석을 예약 상태로 변경하는 메소드
     */
    public void reserve() {
        if (this.isReserved) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.isReserved = true;
    }

    /**
     * 좌석의 예약을 취소하고, 예약 가능 상태로 되돌리는 메소드
     */
    public void cancel() {
        if (!this.isReserved) {
            throw new IllegalStateException("예약되지 않은 좌석은 취소할 수 없습니다.");
        }
        this.isReserved = false;
    }
}