package com.example.ticket_reservation_system.repository;

import com.example.ticket_reservation_system.domain.PerformanceDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * PerformanceDomain에 대한 데이터베이스 작업을 처리하는 Repository 인터페이스
 */
@Repository
public interface PerformanceRepository extends JpaRepository<PerformanceDomain, Long> {
    // 현재는 기본 CRUD 기능만 필요하므로 내부에 추가 메소드는 없습니다.
}