package kr.hhplus.be.server.clean.reservation.domain.entity;

import kr.hhplus.be.server.domain.enums.SeatStatus;

import java.util.UUID;

public class Seat {
    private final UUID id;
    private final int seatNumber;
    private SeatStatus status;
    private final long price;

    // 좌석 번호까지 받는 생성자
    public Seat(UUID id, int seatNumber, SeatStatus status, long price) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.status = status;
        this.price = price;
    }

    public UUID getId() { return id; }
    public int getSeatNumber() { return seatNumber; }
    public SeatStatus getStatus() { return status; }
    public long getPrice() { return price; }

    public void hold() {
        if (status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 예약 중인 좌석");
        }
        this.status = SeatStatus.HOLD;
    }

    public void reserve() {
        if (status != SeatStatus.HOLD) {
            throw new IllegalStateException("HOLD 상태가 아닌 좌석은 예약 불가");
        }
        this.status = SeatStatus.RESERVED;
    }
}
