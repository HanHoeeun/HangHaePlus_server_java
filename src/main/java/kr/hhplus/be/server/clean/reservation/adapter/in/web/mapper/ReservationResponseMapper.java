package kr.hhplus.be.server.clean.reservation.adapter.in.web.mapper;

import kr.hhplus.be.server.clean.reservation.adapter.in.web.response.AvailableSeatsResponse;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.response.ConfirmReservationResponse;
import kr.hhplus.be.server.clean.reservation.adapter.in.web.response.ReserveSeatResponse;
import kr.hhplus.be.server.clean.reservation.application.dto.AvailableSeatsResult;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.domain.enums.ReservationStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ReservationResponseMapper {

    public ReserveSeatResponse toResponse(ReserveSeatResult result) {
        return new ReserveSeatResponse(
                result.reservationId(),
                result.seatId(),
                result.seatNumber(),
                result.status()
        );
    }

    public ConfirmReservationResponse toResponse(UUID reservationId, ReservationStatus status) {
        return new ConfirmReservationResponse(
                reservationId,
                status
        );
    }

    public AvailableSeatsResponse toResponse(AvailableSeatsResult result) {
        var seatInfos = result.seats().stream()
                .map(seat -> new AvailableSeatsResponse.SeatInfo(
                        seat.seatId(),
                        seat.seatNumber(),
                        "AVAILABLE".equals(seat.status()) // status로 isAvailable 계산
                ))
                .toList();

        return new AvailableSeatsResponse(result.showId(), seatInfos);
    }


}
