package kr.hhplus.be.server.layered.catalog.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.enums.SeatStatus;

import java.util.UUID;

@Entity
@Table(name = "seat")
public class Seat {

    @Id
    @Column(name = "seat_id", nullable = false)
    private UUID id;

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "price", nullable = false)
    private long price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    protected Seat() {}

    public Seat(UUID scheduleId, int seatNumber, long price, SeatStatus status) {
        this.id = UUID.randomUUID();
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public long getPrice() { return price; }
    public SeatStatus getStatus() { return status; }
}
