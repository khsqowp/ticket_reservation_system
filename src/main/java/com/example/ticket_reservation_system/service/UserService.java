package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성합니다.
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 회원가입 비즈니스 로직
     * @param requestDTO 회원가입 요청 정보
     * @return 생성된 UserDomain 객체
     */
    @Transactional
    public UserDomain signup(UserSignupRequestDTO requestDTO) {
        // 이메일 중복 확인
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화 로직은 추후 Spring Security 도입 시 추가 예정
        UserDomain user = requestDTO.toEntity();

        return userRepository.save(user);
    }
}