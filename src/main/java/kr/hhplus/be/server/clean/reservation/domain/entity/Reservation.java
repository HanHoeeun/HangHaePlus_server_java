package kr.hhplus.be.server.clean.reservation.domain.entity;

import kr.hhplus.be.server.domain.enums.SeatStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class Reservation {
    private final UUID id;
    private final UUID userId;
    private final UUID seatId;
    private SeatStatus status;
    private final long amount;

    public Reservation(UUID id, UUID userId, UUID seatId, SeatStatus status, long amount) {
        this.id = id;
        this.userId = userId;
        this.seatId = seatId;
        this.status = status;
        this.amount = amount;
    }

    public void confirmPayment() {
        if (status != SeatStatus.HOLD) {
            throw new IllegalStateException("결제 가능한 상태가 아님");
        }
        this.status = SeatStatus.RESERVED;
    }

}
