package kr.hhplus.be.server.clean.reservation.application.dto;

import java.util.UUID;

public record AvailableSeatsResult(
        UUID seatId,
        int seatNumber,
        long price,
        String status
) {}
