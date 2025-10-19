package kr.hhplus.be.server.clean.reservation.adapter.in.web.mapper;

import kr.hhplus.be.server.clean.reservation.adapter.in.web.request.ConfirmReservationRequest;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.request.ReserveSeatRequest;
import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReservationRequestMapper {

    public ReserveSeatCommand toCommand(ReserveSeatRequest req, UUID userId) {
        return new ReserveSeatCommand(
                userId,
                req.showId(),
                req.seatId(),
                req.seatNumber()
        );
    }

    public ConfirmReservationCommand toCommand(ConfirmReservationRequest req, UUID userId) {
        return new ConfirmReservationCommand(
                userId,
                req.reservationId(),
                req.amount()
        );
    }
}
