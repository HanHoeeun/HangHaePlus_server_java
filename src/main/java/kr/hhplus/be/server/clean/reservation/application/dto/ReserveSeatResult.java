// src/main/java/kr/hhplus/be/server/clean/reservation/application/dto/ReserveSeatResult.java
package kr.hhplus.be.server.clean.reservation.application.dto;

import java.util.UUID;

public class ReserveSeatResult {
    private final UUID seatId;
    private final String seatStatus;

    public ReserveSeatResult(UUID seatId, String seatStatus) {
        this.seatId = seatId;
        this.seatStatus = seatStatus;
    }

    public UUID getSeatId() {
        return seatId;
    }

    public String getSeatStatus() {
        return seatStatus;
    }
}
