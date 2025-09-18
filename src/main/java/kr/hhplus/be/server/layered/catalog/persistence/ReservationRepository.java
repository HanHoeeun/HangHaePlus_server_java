package kr.hhplus.be.server.layered.catalog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findActiveHolds(UUID scheduleId, Instant now);
}
