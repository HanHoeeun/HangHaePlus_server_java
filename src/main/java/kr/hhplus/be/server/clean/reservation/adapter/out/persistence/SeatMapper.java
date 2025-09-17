package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.domain.enums.SeatStatus;

public class SeatMapper {

    public static Seat toDomain(SeatJpaEntity entity) {
        return new Seat(entity.getId(), SeatStatus.valueOf(entity.getStatus()), entity.getPrice());
    }

    public static SeatJpaEntity toEntity(Seat seat) {
        return new SeatJpaEntity(seat.getId(), seat.getPrice(), seat.getStatus().name());
    }
}
