package com.example.ticket_reservation_system.service;

import com.example.ticket_reservation_system.config.jwt.JwtUtil;
import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.domain.UserRoleEnum;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${admin.token}")
    private String ADMIN_TOKEN;

    @Transactional
    public UserDomain signup(UserSignupRequestDTO requestDTO) {
        String email = requestDTO.getEmail();

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDTO.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDTO.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        UserDomain user = requestDTO.toEntity(passwordEncoder, role);
        return userRepository.save(user);
    }

    public String login(UserLoginRequestDTO requestDTO) {
        String email = requestDTO.getEmail();
        String password = requestDTO.getPassword();

        UserDomain user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록된 사용자가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.createToken(user.getEmail(), user.getRole());
    }
}