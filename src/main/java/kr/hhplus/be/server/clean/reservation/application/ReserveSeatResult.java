package kr.hhplus.be.server.clean.reservation.application;
import java.time.Instant;
import java.util.UUID;
public record ReserveSeatResult(
        UUID reservationId,
        String status,           // "HELD"
        Instant expiresAt,
        long totalAmount
) {}
