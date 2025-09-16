package kr.hhplus.be.server.layered.catalog.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** 활성 HOLD만 조회(만료 전) */
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    @Query("""
    select r from Reservation r
     where r.scheduleId = :scheduleId
       and r.status = kr.hhplus.be.server.domain.enums.ReservationStatus.HELD
       and r.holdExpiresAt > :now
  """)
    List<Reservation> findActiveHolds(@Param("scheduleId") UUID scheduleId, @Param("now") Instant now);
}
