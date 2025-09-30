package kr.hhplus.be.server.clean.reservation.application.dto;

import java.time.Instant;
import java.util.UUID;

public class ReserveSeatResult {
    private final UUID reservationId;
    private final String status;
    private final Instant expiresAt;
    private final long totalAmount;

    public ReserveSeatResult(UUID reservationId, String status, Instant expiresAt, long totalAmount) {
        this.reservationId = reservationId;
        this.status = status;
        this.expiresAt = expiresAt;
        this.totalAmount = totalAmount;
    }

    public UUID reservationId() {
        return reservationId;
    }

    public String status() {
        return status;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public long totalAmount() {
        return totalAmount;
    }
}
