package kr.hhplus.be.server.clean.reservation.application.dto;
import java.util.UUID;
public record ReserveSeatCommand(
        UUID userId,
        UUID showId,
        UUID seatId,
        int seatNumber
) {}
