package kr.hhplus.be.server.clean.reservation.port;
import java.util.Optional;
import java.util.UUID;
public interface SeatQueryPort {
    Optional<SeatSnapshot> findSeat(UUID showId, int seatNumber);
    record SeatSnapshot(UUID seatId, UUID showId, int seatNumber, long price, String status) {}
}
