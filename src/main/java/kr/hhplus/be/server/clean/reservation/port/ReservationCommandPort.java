package kr.hhplus.be.server.clean.reservation.port;
import java.time.Instant;
import java.util.UUID;
public interface ReservationCommandPort {
    UUID createHeld(UUID userId, UUID showId, int seatNumber, long amount, Instant holdExpiresAt);
}
