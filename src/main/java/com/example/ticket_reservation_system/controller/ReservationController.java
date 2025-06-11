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

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(@Valid @RequestBody ReservationRequestDTO requestDTO) {
        ReservationDomain savedReservation = reservationService.reserveTicket(requestDTO);
        ReservationResponseDTO responseDTO = ReservationResponseDTO.from(savedReservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/my-reservations/{userId}")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(@PathVariable Long userId) {
        List<ReservationDomain> myReservations = reservationService.findMyReservations(userId);
        List<ReservationResponseDTO> responseDTOs = myReservations.stream()
                .map(ReservationResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * 예매를 취소하는 API
     * @param reservationId 취소할 예매의 ID
     * @return 성공 시 204 No Content 상태 코드
     */
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
