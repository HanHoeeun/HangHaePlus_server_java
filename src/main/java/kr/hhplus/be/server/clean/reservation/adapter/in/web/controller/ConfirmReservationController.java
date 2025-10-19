package kr.hhplus.be.server.clean.reservation.adapter.in.web.controller;

import kr.hhplus.be.server.clean.reservation.adapter.in.web.mapper.ReservationRequestMapper;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.mapper.ReservationResponseMapper;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.request.ConfirmReservationRequest;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.response.ConfirmReservationResponse;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ConfirmReservationController {

    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final ReservationRequestMapper requestMapper;
    private final ReservationResponseMapper responseMapper;

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmReservationResponse> confirmReservation(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody ConfirmReservationRequest request
    ) {
        // Request → Command
        var command = requestMapper.toCommand(request, userId);

        // UseCase 호출
        var result = confirmReservationUseCase.confirm(command);

        // Result → Response
        var response = responseMapper.toResponse(result.reservationId(), result.status());

        return ResponseEntity.ok(response);
    }
}
