package kr.hhplus.be.server.clean.reservation.port.out;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SeatRepositoryPort {
    Seat save(Seat seat);

    Optional<Seat> findById(UUID seatId);

    List<Seat> findByShowId(UUID showId);
}
