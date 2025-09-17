package kr.hhplus.be.server.clean.reservation.port.out;

import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepositoryPort {
    Optional<Reservation> findById(UUID reservationId);

    Reservation save(Reservation reservation);
}
