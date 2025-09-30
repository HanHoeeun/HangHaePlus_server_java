package kr.hhplus.be.server.clean.reservation.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataSeatRepository extends JpaRepository<SeatJpaEntity, UUID> {

}
