package kr.hhplus.be.server.clean.reservation.port.in;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;

public interface ConfirmReservationUseCase {
    Reservation confirm(ConfirmReservationCommand command);
}
