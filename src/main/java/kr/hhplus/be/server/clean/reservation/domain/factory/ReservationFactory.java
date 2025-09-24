package kr.hhplus.be.server.clean.reservation.domain.factory;

import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.domain.enums.SeatStatus;

import java.util.UUID;

public class ReservationFactory {

    public static Reservation createHoldReservation(UUID userId, UUID seatId, long amount) {
        return new Reservation(
                UUID.randomUUID(),   // 예약 ID
                userId,
                seatId,
                SeatStatus.HOLD,
                amount
        );
    }
}
