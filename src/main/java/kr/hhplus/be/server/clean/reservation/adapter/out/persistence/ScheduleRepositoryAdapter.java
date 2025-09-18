package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.ScheduleRepositoryPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class ScheduleRepositoryAdapter implements ScheduleRepositoryPort {

    @Override
    public List<LocalDate> findAvailableDates() {
        return List.of(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
    }

    @Override
    public List<Seat> findSeatsBySchedule(UUID scheduleId) {
        return List.of(
                new Seat(UUID.randomUUID(), 1, SeatStatus.AVAILABLE, 10000),
                new Seat(UUID.randomUUID(), 2, SeatStatus.SOLD, 10000)
        );
    }
}
