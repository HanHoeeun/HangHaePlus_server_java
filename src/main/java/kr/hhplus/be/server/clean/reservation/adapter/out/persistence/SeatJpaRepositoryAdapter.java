package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SeatJpaRepositoryAdapter implements SeatRepositoryPort {

    private final SpringDataSeatRepository seatRepository;

    @Override
    public Seat save(Seat seat) {
        SeatJpaEntity entity = SeatMapper.toEntity(seat, UUID.randomUUID());
        SeatJpaEntity saved = seatRepository.save(entity);
        return SeatMapper.toDomain(saved);
    }

    @Override
    public Optional<Seat> findById(UUID seatId) {
        return seatRepository.findById(seatId)
                .map(SeatMapper::toDomain);
    }

    @Override
    public List<Seat> findByShowId(UUID showId) {
        return seatRepository.findByShowId(showId).stream()
                .map(SeatMapper::toDomain)
                .toList();
    }
}
