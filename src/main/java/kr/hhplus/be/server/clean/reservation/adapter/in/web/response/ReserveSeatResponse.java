package kr.hhplus.be.server.clean.reservation.adapter.in.web.response;

import kr.hhplus.be.server.domain.enums.SeatStatus;
import java.util.UUID;

public record ReserveSeatResponse(
        UUID reservationId,
        UUID seatId,
        int seatNumber,
        SeatStatus status
) {}
