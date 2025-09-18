package kr.hhplus.be.server.clean.reservation.port.out;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleRepositoryPort {
    List<LocalDate> findAvailableDates();
    List<Seat> findSeatsBySchedule(UUID scheduleId);
}
