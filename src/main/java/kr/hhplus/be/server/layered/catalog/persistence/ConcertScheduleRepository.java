package kr.hhplus.be.server.layered.catalog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, UUID> {
    List<ConcertSchedule> findByConcertIdOrderByShowAtAsc(UUID concertId);
}
