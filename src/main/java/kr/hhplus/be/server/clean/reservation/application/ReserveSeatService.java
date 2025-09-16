package kr.hhplus.be.server.clean.reservation.application;

import kr.hhplus.be.server.clean.reservation.port.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatQueryPort seatQuery;
    private final ReservationQueryPort reservationQuery;
    private final ReservationCommandPort reservationCmd;
    private final SeatLockPort lock;
    private final TimeProvider time;
    private final Duration holdTtl;         // ex) PT5M
    private final Duration lockTtl;         // ex) PT1S (임계구간 보호용)

    public ReserveSeatService(SeatQueryPort seatQuery,
                              ReservationQueryPort reservationQuery,
                              ReservationCommandPort reservationCmd,
                              SeatLockPort lock,
                              TimeProvider time,
                              Duration holdTtl,
                              Duration lockTtl) {
        this.seatQuery = seatQuery;
        this.reservationQuery = reservationQuery;
        this.reservationCmd = reservationCmd;
        this.lock = lock;
        this.time = time;
        this.holdTtl = holdTtl;
        this.lockTtl = lockTtl;
    }

    @Override
    @Transactional
    public ReserveSeatResult reserve(ReserveSeatCommand cmd) {
        var seat = seatQuery.findSeat(cmd.showId(), cmd.seatNumber())
                .orElseThrow(() -> new NotFoundException("SEAT_NOT_FOUND"));

        // 빠른 SOLD 차단
        if ("SOLD".equals(seat.status())) {
            throw new ConflictException("SEAT_ALREADY_SOLD");
        }

        String key = SeatLockPort.key(cmd.showId(), cmd.seatNumber());
        if (!lock.tryLock(key, lockTtl.toMillis())) {
            throw new ConflictException("SEAT_LOCKED_TRY_AGAIN");
        }
        try {
            Instant now = time.now();

            // 활성 HOLD 존재? (만료 전)
            if (reservationQuery.existsActiveHold(cmd.showId(), cmd.seatNumber(), now)) {
                throw new ConflictException("SEAT_HELD_BY_OTHERS");
            }
            // SOLD 다시 확인(다중 인스턴스)
            if (reservationQuery.isSeatSold(cmd.showId(), cmd.seatNumber())) {
                throw new ConflictException("SEAT_ALREADY_SOLD");
            }

            Instant exp = now.plus(holdTtl);
            UUID rid = reservationCmd.createHeld(cmd.userId(), cmd.showId(), cmd.seatNumber(), seat.price(), exp);

            return new ReserveSeatResult(rid, "HELD", exp, seat.price());

        } catch (DuplicateActiveReservationException e) {
            // DB 부분 유니크 충돌
            throw new ConflictException("SEAT_HELD_BY_OTHERS");
        } finally {
            lock.unlock(key); // 임계구간 락 해제(홀드 유지 책임은 DB에 있음)
        }
    }

    // -------- 오류 타입 --------
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String code) { super(code); }
    }
    public static class ConflictException extends RuntimeException {
        public ConflictException(String code) { super(code); }
    }
    public static class DuplicateActiveReservationException extends RuntimeException {}
}
