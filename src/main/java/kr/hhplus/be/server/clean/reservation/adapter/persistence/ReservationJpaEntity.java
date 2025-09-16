package kr.hhplus.be.server.clean.reservation.adapter.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class ReservationJpaEntity {

    @Id
    @Column(name = "reservation_id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "status", nullable = false)
    private String status; // HELD/CONFIRMED/CANCELED/EXPIRED

    @Column(name = "hold_expires_at")
    private Instant holdExpiresAt;

    @Column(name = "amount", nullable = false)
    private long amount;

    @Version
    private int version;

    protected ReservationJpaEntity() { }

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

    // --- getters ---
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public String getStatus() { return status; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }
    public long getAmount() { return amount; }
    public int getVersion() { return version; }

    // --- setters ---
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setScheduleId(UUID scheduleId) { this.scheduleId = scheduleId; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public void setStatus(String status) { this.status = status; }
    public void setHoldExpiresAt(Instant holdExpiresAt) { this.holdExpiresAt = holdExpiresAt; }
    public void setAmount(long amount) { this.amount = amount; }
}
