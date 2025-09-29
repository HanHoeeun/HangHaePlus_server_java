package kr.hhplus.be.server.clean.reservation.application.dto;

import java.util.UUID;

public record ConfirmReservationCommand(UUID userId, UUID reservationId, long amount) {}
