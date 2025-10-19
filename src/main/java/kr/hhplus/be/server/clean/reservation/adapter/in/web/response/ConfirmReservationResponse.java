package kr.hhplus.be.server.clean.reservation.adapter.in.web.response;

import kr.hhplus.be.server.domain.enums.ReservationStatus;
import java.util.UUID;

public record ConfirmReservationResponse(
        UUID reservationId,
        ReservationStatus status
) {}
