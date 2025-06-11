package com.example.ticket_reservation_system.repository;

import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ReservationDomain에 대한 데이터베이스 작업을 처리하는 Repository 인터페이스
 */
@Repository
public interface ReservationRepository extends JpaRepository<ReservationDomain, Long> {
    /**
     * 특정 사용자의 모든 예매 내역을 조회합니다.
     * @param user 조회할 사용자
     * @return 예매 내역 리스트
     */
    List<ReservationDomain> findAllByUser(UserDomain user);
}