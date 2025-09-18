package kr.hhplus.be.server.layered.catalog.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @Column(name = "reservation_id", nullable = false)
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "hold_expires_at", nullable = false)
    private Instant holdExpiresAt;

    protected Reservation() {}

    public Reservation(UUID scheduleId, int seatNumber, UUID userId, Instant holdExpiresAt) {
        this.id = UUID.randomUUID();
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.userId = userId;
        this.holdExpiresAt = holdExpiresAt;
    }

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public UUID getUserId() { return userId; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
}
