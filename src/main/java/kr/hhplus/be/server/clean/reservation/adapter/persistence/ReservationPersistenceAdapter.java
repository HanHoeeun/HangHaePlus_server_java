package kr.hhplus.be.server.clean.reservation.adapter.persistence;

import kr.hhplus.be.server.clean.reservation.application.ReserveSeatService.DuplicateActiveReservationException;
import kr.hhplus.be.server.clean.reservation.port.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
public class ReservationPersistenceAdapter
        implements SeatQueryPort, ReservationQueryPort, ReservationCommandPort {

    private final SeatJpaRepository seatRepo;
    private final ReservationJpaRepository resRepo;

    public ReservationPersistenceAdapter(SeatJpaRepository seatRepo, ReservationJpaRepository resRepo) {
        this.seatRepo = seatRepo; this.resRepo = resRepo;
    }

    // SeatQueryPort
    @Override public Optional<SeatQueryPort.SeatSnapshot> findSeat(UUID showId, int seatNumber) {
        return seatRepo.findByScheduleIdAndSeatNumber(showId, seatNumber)
                .map(s -> new SeatQueryPort.SeatSnapshot(s.getId(), s.getScheduleId(), s.getSeatNumber(), s.getPrice(), s.getStatus()));
    }

    // ReservationQueryPort
    @Override public boolean existsActiveHold(UUID showId, int seatNumber, Instant now) {
        return resRepo.existsActiveHold(showId, seatNumber, now);
    }
    @Override public boolean isSeatSold(UUID showId, int seatNumber) {
        return seatRepo.existsByScheduleIdAndSeatNumberAndStatus(showId, seatNumber, "SOLD");
    }

    // ReservationCommandPort
    @Override public UUID createHeld(UUID userId, UUID showId, int seatNumber, long amount, Instant exp) {
        var e = new ReservationJpaEntity();
        e.setUserId(userId);
        e.setScheduleId(showId);
        e.setSeatNumber(seatNumber);
        e.setStatus("HELD");
        e.setAmount(amount);
        e.setHoldExpiresAt(exp);
        try {
            resRepo.saveAndFlush(e);
            return e.getId();
        } catch (DataIntegrityViolationException ex) {
            // 부분 유니크 인덱스에 걸림
            throw new DuplicateActiveReservationException();
        }
    }
}
