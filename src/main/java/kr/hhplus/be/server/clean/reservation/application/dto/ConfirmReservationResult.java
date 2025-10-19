package kr.hhplus.be.server.clean.reservation.application.dto;

import kr.hhplus.be.server.domain.enums.ReservationStatus;
import java.util.UUID;

public record ConfirmReservationResult(
        UUID reservationId,
        ReservationStatus status
) {}
