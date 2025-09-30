package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatJpaRepositoryAdapter implements SeatRepositoryPort {

    private final SpringDataSeatRepository repository;

    @Override
    public Optional<Seat> findById(UUID seatId) {
        return repository.findById(seatId).map(SeatMapper::toDomain);
    }

    @Override
    public Seat save(Seat seat) {
        return SeatMapper.toDomain(repository.save(SeatMapper.toEntity(seat)));
    }
}
