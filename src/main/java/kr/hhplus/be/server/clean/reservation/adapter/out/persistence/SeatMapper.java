package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public Seat toDomain(SeatJpaEntity entity) {
        return new Seat(
                entity.getId(),
                entity.getSeatNumber(),
                SeatStatus.valueOf(entity.getStatus()),
                entity.getPrice()
        );
    }

    public SeatJpaEntity toEntity(Seat seat) {
        return new SeatJpaEntity(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getStatus().name(),
                seat.getPrice()
        );
    }
}
