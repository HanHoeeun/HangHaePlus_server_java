package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSeatRepository extends JpaRepository<SeatJpaEntity, UUID> {
    List<SeatJpaEntity> findByShowId(UUID showId);
}
