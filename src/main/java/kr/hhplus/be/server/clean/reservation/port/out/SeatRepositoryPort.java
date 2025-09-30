package kr.hhplus.be.server.clean.reservation.port.out;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;

import java.util.Optional;
import java.util.UUID;

public interface SeatRepositoryPort {
    Optional<Seat> findById(UUID seatId);

    Seat save(Seat seat);
}
