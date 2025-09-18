package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.AvailableDatesResult;
import kr.hhplus.be.server.clean.reservation.application.dto.AvailableSeatsResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ReservationQueryUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.ScheduleRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ReservationQueryService implements ReservationQueryUseCase {

    private final ScheduleRepositoryPort scheduleRepository;

    @Override
    public List<AvailableDatesResult> getAvailableDates() {
        return scheduleRepository.findAvailableDates().stream()
                .map(AvailableDatesResult::new)
                .toList();
    }

    @Override
    public List<AvailableSeatsResult> getAvailableSeats(UUID scheduleId) {
        return scheduleRepository.findSeatsBySchedule(scheduleId).stream()
                .map(seat -> new AvailableSeatsResult(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getPrice(),
                        seat.getStatus().name()
                ))
                .toList();
    }
}
