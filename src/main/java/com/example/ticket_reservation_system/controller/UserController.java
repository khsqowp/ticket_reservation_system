package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.UserDomain;
import com.example.ticket_reservation_system.dto.UserLoginRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupRequestDTO;
import com.example.ticket_reservation_system.dto.UserSignupResponseDTO;
import com.example.ticket_reservation_system.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDTO> signup(@Valid @RequestBody UserSignupRequestDTO requestDTO) {
        UserDomain savedUser = userService.signup(requestDTO);
        UserSignupResponseDTO responseDTO = UserSignupResponseDTO.from(savedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDTO requestDTO, HttpServletResponse response) {
        String token = userService.login(requestDTO);
        response.setHeader("Authorization", token);
        return ResponseEntity.ok("로그인에 성공했습니다.");
    }
}