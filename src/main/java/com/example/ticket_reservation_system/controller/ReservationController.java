package com.example.ticket_reservation_system.controller;

import com.example.ticket_reservation_system.domain.ReservationDomain;
import com.example.ticket_reservation_system.dto.ReservationRequestDTO;
import com.example.ticket_reservation_system.dto.ReservationResponseDTO;
import com.example.ticket_reservation_system.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 예매와 관련된 HTTP 요청을 처리하는 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 티켓을 예매하는 API
     * @param requestDTO 예매 요청 DTO (userId, seatId)
     * @return 생성된 예매 정보와 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO requestDTO) {
        ReservationDomain savedReservation = reservationService.reserveTicket(requestDTO);
        ReservationResponseDTO responseDTO = ReservationResponseDTO.from(savedReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * 특정 사용자의 모든 예매 내역을 조회하는 API
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 예매 내역 리스트와 200 OK 상태 코드
     */
    @GetMapping("/my-reservations/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(@PathVariable Long userId) {
        List<ReservationDomain> myReservations = reservationService.findMyReservations(userId);
        List<ReservationResponseDTO> responseDTOs = myReservations.stream()
                .map(ReservationResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }
}