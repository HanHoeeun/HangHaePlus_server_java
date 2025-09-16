package kr.hhplus.be.server.clean.reservation.port;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
public interface ReservationQueryPort {
    boolean existsActiveHold(UUID showId, int seatNumber, Instant now);
    boolean isSeatSold(UUID showId, int seatNumber);
}
