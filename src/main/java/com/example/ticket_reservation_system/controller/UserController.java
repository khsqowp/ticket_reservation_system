package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserLoginResponseDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupResponseDTO;
import com.example.ticket_reservation_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자와 관련된 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    /**
     * 사용자 회원가입 API
     * @param requestDTO 회원가입 요청 DTO
     * @return 생성된 사용자의 정보와 201 Created 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDTO> signup(@Valid @RequestBody UserSignupRequestDTO requestDTO) {
        UserDomain savedUser = userService.signup(requestDTO);
        UserSignupResponseDTO responseDTO = UserSignupResponseDTO.from(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * 사용자 로그인 API
     * @param requestDTO 로그인 요청 DTO
     * @return 로그인 성공 메시지, 임시 토큰과 200 OK 상태 코드
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO) {
        UserDomain loginUser = userService.login(requestDTO);
        String simpleToken = "temp-token-for-" + loginUser.getEmail();
        UserLoginResponseDTO responseDTO = new UserLoginResponseDTO("로그인에 성공했습니다.", simpleToken);
        return ResponseEntity.ok(responseDTO);
    }
}
