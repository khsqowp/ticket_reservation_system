package com.example.ticket_reservation_system.repository;

import com.example.ticket_reservation_system.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserDomain에 대한 데이터베이스 작업을 처리하는 Repository 인터페이스
 */
@Repository
public interface UserRepository extends JpaRepository<UserDomain, Long> {

    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 사용자 이메일
     * @return Optional<UserDomain>
     */
    Optional<UserDomain> findByEmail(String email);

}