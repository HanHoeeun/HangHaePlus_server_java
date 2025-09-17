package kr.hhplus.be.server.clean.catalog.application.dto;

import java.time.Instant;

public record SeatAvailabilityDto(
        int seatNumber,
        long price,
        String label,           // AVAILABLE | HELD_BY_SELF | HELD_BY_OTHERS | SOLD
        Instant holdExpiresAt   // 만료 예정 시각
) {}
