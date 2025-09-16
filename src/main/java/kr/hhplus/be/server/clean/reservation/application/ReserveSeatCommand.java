package kr.hhplus.be.server.clean.reservation.application;
import java.util.UUID;
public record ReserveSeatCommand(
        UUID userId,
        UUID showId,
        int seatNumber
) {}
