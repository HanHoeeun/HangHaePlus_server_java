package kr.hhplus.be.server.clean.reservation.adapter.in.web.response;

import java.util.List;
import java.util.UUID;

public record AvailableSeatsResponse(
        UUID showId,
        List<SeatInfo> seats
) {
    public record SeatInfo(
            UUID seatId,
            int seatNumber,
            boolean isAvailable
    ) {}
}

