package kr.hhplus.be.server.clean.reservation.application.dto;

import kr.hhplus.be.server.domain.enums.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public class ReserveSeatResult {
    private final UUID reservationId;
    private final UUID seatId;
//    private final String status;
    private final int seatNumber;
    private final ReservationStatus status;
    private final Instant expiresAt;
    private final long totalAmount;

    public ReserveSeatResult(
            UUID reservationId,
            UUID seatId,
            int seatNumber,
            ReservationStatus status,
            Instant expiresAt,
            long totalAmount
    ) {
        this.reservationId = reservationId;
        this.seatId = seatId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.expiresAt = expiresAt;
        this.totalAmount = totalAmount;
    }

    public UUID reservationId() { return reservationId; }
    public UUID seatId() { return seatId; }
    public int seatNumber() { return seatNumber; }
    public ReservationStatus status() { return status; }
    public Instant expiresAt() { return expiresAt; }
    public long totalAmount() { return totalAmount; }
}