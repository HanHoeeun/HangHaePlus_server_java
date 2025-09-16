// src/main/java/kr/hhplus/be/server/clean/reservation/adapter/persistence/SeatJpaEntity.java
package kr.hhplus.be.server.clean.reservation.adapter.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "seat")
public class SeatJpaEntity {

    @Id
    @Column(name = "seat_id", nullable = false)
    private UUID id;                    // ← 필드명이 id

    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @Column(name = "seat_number", nullable = false)
    private int seatNumber;

    @Column(name = "price", nullable = false)
    private long price;

    @Column(name = "status", nullable = false)
    private String status;              // "AVAILABLE" | "SOLD"

    protected SeatJpaEntity() {}        // JPA 기본 생성자

    // ----- getters -----
    public UUID getId() { return id; }
    public UUID getScheduleId() { return scheduleId; }
    public int getSeatNumber() { return seatNumber; }
    public long getPrice() { return price; }
    public String getStatus() { return status; }


}
