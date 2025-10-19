package kr.hhplus.be.server.clean.reservation.adapter.in.web.controller;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

    private record Req(UUID showId, List<UUID> seatIds) {} // ✅ seatId는 UUID 타입
    private record Res(UUID reservationId, String status, String expiresAt, long totalAmount) {}

    private final ReserveSeatUseCase reserveSeat;

    public ReservationController(ReserveSeatUseCase reserveSeat) {
        this.reserveSeat = reserveSeat;
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> reserve(
            @RequestBody Req req,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        if (req.seatIds() == null || req.seatIds().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", "VALIDATION_ERROR",
                    "message", "seatIds required"
            ));
        }

        // 단일 좌석 예약 처리
        UUID seatId = req.seatIds().get(0);
        int seatNumber = 1; // 필요시 Seat 조회로 실제 seatNumber 계산 가능

        var result = reserveSeat.reserve(
                new ReserveSeatCommand(userId, req.showId(), seatId, seatNumber)
        );

        return ResponseEntity.status(201).body(Map.of(
                "reservationId", result.reservationId(),
                "status", result.status(),
                "expiresAt", result.expiresAt().toString(),
                "totalAmount", result.totalAmount()
        ));
    }
}
