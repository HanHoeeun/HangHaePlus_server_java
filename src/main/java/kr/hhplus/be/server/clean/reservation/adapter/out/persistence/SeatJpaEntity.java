package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "seat")
public class SeatJpaEntity {

    @Id
    @Column(name = "seat_id", nullable = false)
    private UUID id;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "status", nullable = false)
    private String status; // AVAILABLE, HOLD, RESERVED

    protected SeatJpaEntity() {}

    public SeatJpaEntity(UUID id, long price, String status) {
        this.id = id;
        this.price = price;
        this.status = status;
    }

    public UUID getId() { return id; }
    public long getPrice() { return price; }
    public String getStatus() { return status; }
}
