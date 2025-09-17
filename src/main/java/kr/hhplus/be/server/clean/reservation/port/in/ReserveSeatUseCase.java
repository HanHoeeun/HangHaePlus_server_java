package kr.hhplus.be.server.clean.reservation.port.in;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;

public interface ReserveSeatUseCase {
    ReserveSeatResult reserve(ReserveSeatCommand cmd);
}
