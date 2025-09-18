package kr.hhplus.be.server.layered.catalog.dto;

import java.time.Instant;
import java.util.UUID;

public record ShowAvailabilityDto(
        UUID scheduleId,
        Instant showAt,
        int totalSeats,
        int availableSeats // SOLD 제외 + (HELD active 제외)
) {}
