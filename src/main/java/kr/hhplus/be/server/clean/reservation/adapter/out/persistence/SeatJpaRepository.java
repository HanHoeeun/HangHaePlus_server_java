package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SeatJpaRepository extends JpaRepository<SeatJpaEntity, UUID> {
    Optional<SeatJpaEntity> findByScheduleIdAndSeatNumber(UUID scheduleId, int seatNumber);

    boolean existsByScheduleIdAndSeatNumberAndStatus(UUID scheduleId, int seatNumber, String status);
}
