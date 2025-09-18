package kr.hhplus.be.server.clean.reservation.port.out;

import java.util.UUID;

public interface SeatLockPort {
    void lock(UUID seatId);

    void unlock(UUID seatId);
}
