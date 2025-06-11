package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.SeatDomain;
import com.example.ticket_reservation_system.dto.SeatRequestDTO;
import com.example.ticket_reservation_system.dto.SeatResponseDTO;
import com.example.ticket_reservation_system.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 좌석과 관련된 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    /**
     * 새로운 좌석을 등록하는 API
     * @param requestDTO 등록할 좌석 정보 DTO
     * @return 생성된 좌석 정보와 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<SeatResponseDTO> register(@Valid @RequestBody SeatRequestDTO requestDTO) {
        SeatDomain savedSeat = seatService.registerSeat(requestDTO);
        SeatResponseDTO responseDTO = SeatResponseDTO.from(savedSeat);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
