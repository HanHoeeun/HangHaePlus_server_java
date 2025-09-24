package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class ReservationJpaEntity {

    @Id
    @Column(name = "reservation_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @Column(name = "status", nullable = false)
    private String status; // HOLD, RESERVED

    @Column(name = "amount", nullable = false)
    private long amount;

    protected ReservationJpaEntity() {}

    public ReservationJpaEntity(UUID id, UUID userId,UUID seatId, String status, long amount) {
        this.id = id;
        this.userId = userId;
        this.seatId = seatId;
        this.status = status;
        this.amount = amount;
    }

    public UUID getId() { return id; }
    public UUID getUserId(){ return userId; }
    public UUID getSeatId() { return seatId; }
    public String getStatus() { return status; }
    public long getAmount() { return amount; }
}
