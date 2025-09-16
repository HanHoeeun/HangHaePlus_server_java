package kr.hhplus.be.server.clean.reservation.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface SeatJpaRepository extends JpaRepository<SeatJpaEntity, UUID> {
    Optional<SeatJpaEntity> findByScheduleIdAndSeatNumber(UUID scheduleId, int seatNumber);
    boolean existsByScheduleIdAndSeatNumberAndStatus(UUID scheduleId, int seatNumber, String status); // SOLD 검사
}
