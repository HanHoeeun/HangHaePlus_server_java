package kr.hhplus.be.server.clean.reservation.port.in;

import kr.hhplus.be.server.clean.reservation.application.dto.AvailableDatesResult;
import kr.hhplus.be.server.clean.reservation.application.dto.AvailableSeatsResult;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReservationQueryUseCase {
    List<AvailableDatesResult> getAvailableDates();
    List<AvailableSeatsResult> getAvailableSeats(UUID scheduleId);
}
