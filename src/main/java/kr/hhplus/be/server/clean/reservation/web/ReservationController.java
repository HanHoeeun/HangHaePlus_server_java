package kr.hhplus.be.server.clean.reservation.web;

import kr.hhplus.be.server.clean.reservation.application.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ReservationController {

    private record Req(UUID showId, List<Integer> seatIds) {}
    private record Res(UUID reservationId, String status, String expiresAt, long totalAmount) {}

    private final ReserveSeatUseCase reserveSeat;

    public ReservationController(ReserveSeatUseCase reserveSeat) {
        this.reserveSeat = reserveSeat;
    }

    @PostMapping("/reservations")
    public ResponseEntity<?> reserve(@RequestBody Req req,
                                     @RequestHeader("X-User-Id") UUID userId) {
        if (req.seatIds() == null || req.seatIds().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("code","VALIDATION_ERROR","message","seatIds required"));

        int seatNo = req.seatIds().get(0); // 1좌석 처리
        var result = reserveSeat.reserve(new ReserveSeatCommand(userId, req.showId(), seatNo));
        return ResponseEntity.status(201).body(Map.of(
                "reservationId", result.reservationId(),
                "status", result.status(),
                "expiresAt", result.expiresAt().toString(),
                "totalAmount", result.totalAmount()
        ));
    }
}
