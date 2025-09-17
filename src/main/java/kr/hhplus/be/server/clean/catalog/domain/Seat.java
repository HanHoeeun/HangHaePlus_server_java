package kr.hhplus.be.server.clean.catalog.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "seat")
public class Seat {
    @Id
    @Column(name = "seat_id")
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "price", nullable = false)
    private long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private kr.hhplus.be.server.domain.enums.SeatStatus status; // 없으면 SeatStatus로 대체

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public long getPrice() { return price; }
    public kr.hhplus.be.server.domain.enums.SeatStatus getStatus() { return status; }
}
