package kr.hhplus.be.server.clean.reservation.application.dto;

import java.util.UUID;

public record ConfirmReservationCommand(UUID reservationId, long amount) {}
