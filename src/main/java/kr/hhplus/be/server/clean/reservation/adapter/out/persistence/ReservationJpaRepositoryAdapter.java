package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.clean.reservation.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReservationJpaRepositoryAdapter implements ReservationRepositoryPort {

    private final SpringDataReservationRepository repository;

    @Override
    public Optional<Reservation> findById(UUID reservationId) {
        return repository.findById(reservationId).map(ReservationMapper::toDomain);
    }

    @Override
    public Reservation save(Reservation reservation) {
        return ReservationMapper.toDomain(repository.save(ReservationMapper.toEntity(reservation)));
    }
}
