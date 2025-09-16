package kr.hhplus.be.server.layered.catalog.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @Column(name = "reservation_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private kr.hhplus.be.server.domain.enums.ResevationStatus status;

    @Column(name = "hold_expires_at")
    private Instant holdExpiresAt;

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public kr.hhplus.be.server.domain.enums.ResevationStatus getStatus() { return status; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
}
