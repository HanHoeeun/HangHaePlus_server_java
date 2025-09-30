package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.domain.enums.SeatStatus;

public class ReservationMapper {

    public static Reservation toDomain(ReservationJpaEntity entity) {
        return new Reservation(entity.getId(),
                entity.getSeatId(),
                SeatStatus.valueOf(entity.getStatus()),
                entity.getAmount());
    }

    public static ReservationJpaEntity toEntity(Reservation reservation) {
        return new ReservationJpaEntity(reservation.getId(),
                reservation.getSeatId(),
                reservation.getStatus().name(),
                reservation.getAmount());
    }
}
