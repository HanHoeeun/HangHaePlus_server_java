package kr.hhplus.be.server.clean.reservation.adapter.in.web.request;

import java.util.UUID;

public record ConfirmReservationRequest(
        UUID reservationId,
        long amount
) {}
