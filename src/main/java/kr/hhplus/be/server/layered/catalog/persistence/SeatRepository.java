package kr.hhplus.be.server.layered.catalog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findByScheduleIdOrderBySeatNumberAsc(UUID scheduleId);
}
