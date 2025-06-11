package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 사용자 회원가입 비즈니스 로직
     * @param requestDTO 회원가입 요청 정보
     * @return 생성된 UserDomain 객체
     */
    @Transactional
    public UserDomain signup(UserSignupRequestDTO requestDTO) {
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        UserDomain user = requestDTO.toEntity();
        return userRepository.save(user);
    }

    /**
     * 사용자 로그인 비즈니스 로직
     * @param requestDTO 로그인 요청 정보
     * @return 로그인 성공 시 UserDomain 객체
     */
    @Transactional(readOnly = true)
    public UserDomain login(UserLoginRequestDTO requestDTO) {
        UserDomain user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!user.getPassword().equals(requestDTO.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}