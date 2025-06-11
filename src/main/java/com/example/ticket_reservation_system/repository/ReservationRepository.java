package com.example.ticket_reservation_system.repository;

import com.example.ticket_reservation_system.domain.ReservationDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ReservationDomain에 대한 데이터베이스 작업을 처리하는 Repository 인터페이스
 */
@Repository
public interface ReservationRepository extends JpaRepository<ReservationDomain, Long> {
}