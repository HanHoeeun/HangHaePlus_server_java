package kr.hhplus.be.server.clean.reservation.adapter.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.UUID;

public interface ReservationJpaRepository extends JpaRepository<ReservationJpaEntity, UUID> {

    @Query("""
    select count(r)>0 from ReservationJpaEntity r
     where r.scheduleId = :sid and r.seatNumber = :no
       and r.status = 'HELD' and r.holdExpiresAt > :now
  """)
    boolean existsActiveHold(@Param("sid") UUID scheduleId, @Param("no") int seatNumber, @Param("now") Instant now);

}
