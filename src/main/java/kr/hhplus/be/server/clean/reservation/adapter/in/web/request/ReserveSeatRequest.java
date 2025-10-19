package kr.hhplus.be.server.clean.reservation.adapter.in.web.request;

import java.util.UUID;

public record ReserveSeatRequest(
        UUID showId,
        UUID seatId,
        int seatNumber
) {}
