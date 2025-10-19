package kr.hhplus.be.server.clean.reservation.port.in;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationResult;

public interface ConfirmReservationUseCase {
    ConfirmReservationResult confirm(ConfirmReservationCommand command);
}
