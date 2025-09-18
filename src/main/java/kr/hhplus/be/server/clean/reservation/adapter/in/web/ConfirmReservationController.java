package kr.hhplus.be.server.clean.reservation.adapter.in.web;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ConfirmReservationController {

    private final ConfirmReservationUseCase confirmReservationUseCase;

    @PostMapping("/{reservationId}/confirm")
    public Reservation confirm(
            @PathVariable UUID reservationId,
            @RequestParam long amount
    ) {
        return confirmReservationUseCase.confirm(
                new ConfirmReservationCommand(reservationId, amount)
        );
    }
}
