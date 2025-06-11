package com.example.ticket_reservation_system.repository;

import com.example.ticket_reservation_system.domain.SeatDomain;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SeatDomain에 대한 데이터베이스 작업을 처리하는 Repository 인터페이스
 */
@Repository
public interface SeatRepository extends JpaRepository<SeatDomain, Long> {
    /**
     * 비관적 쓰기 락(Pessimistic Write Lock)을 사용하여 좌석 정보를 조회합니다.
     * 다른 트랜잭션이 이 좌석 데이터에 접근하는 것을 막고 데이터 정합성을 보장합니다.
     * @param id 조회할 좌석의 ID
     * @return Optional<SeatDomain>
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SeatDomain s where s.id = :id")
    Optional<SeatDomain> findByIdWithPessimisticLock(@Param("id") Long id);
}