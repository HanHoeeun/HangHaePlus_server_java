package kr.hhplus.be.server.layered.catalog.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, java.util.UUID> {
    List<ConcertSchedule> findByConcertIdOrderByShowAtAsc(java.util.UUID concertId);
}
