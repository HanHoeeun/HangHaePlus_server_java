package kr.hhplus.be.server.clean.reservation;

import kr.hhplus.be.server.clean.reservation.application.*;
import kr.hhplus.be.server.clean.reservation.port.*;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReserveSeatServiceTest {

    SeatQueryPort seatQ = mock(SeatQueryPort.class);
    ReservationQueryPort resQ = mock(ReservationQueryPort.class);
    ReservationCommandPort resC = mock(ReservationCommandPort.class);
    SeatLockPort lock = mock(SeatLockPort.class);
    TimeProvider time = () -> Instant.parse("2025-09-03T12:00:00Z");

    ReserveSeatUseCase sut = new ReserveSeatService(seatQ, resQ, resC, lock, time,
            Duration.ofMinutes(5), Duration.ofSeconds(1));

    @Test
    void reserve_success_to_HELD() {
        UUID show = UUID.randomUUID(); int no = 12; UUID user = UUID.randomUUID();

        when(seatQ.findSeat(show, no)).thenReturn(Optional.of(new SeatQueryPort.SeatSnapshot(
                UUID.randomUUID(), show, no, 150000, "AVAILABLE"
        )));
        when(lock.tryLock(anyString(), anyLong())).thenReturn(true);
        when(resQ.existsActiveHold(eq(show), eq(no), any())).thenReturn(false);
        when(resQ.isSeatSold(show, no)).thenReturn(false);
        UUID rid = UUID.randomUUID();
        when(resC.createHeld(eq(user), eq(show), eq(no), eq(150000L), any())).thenReturn(rid);

        var out = sut.reserve(new ReserveSeatCommand(user, show, no));

        assertEquals("HELD", out.status());
        assertEquals(rid, out.reservationId());
        verify(lock).unlock(anyString());
    }

    @Test
    void conflict_when_already_held() {
        UUID show = UUID.randomUUID(); int no = 1; UUID user = UUID.randomUUID();
        when(seatQ.findSeat(show, no)).thenReturn(Optional.of(new SeatQueryPort.SeatSnapshot(
                UUID.randomUUID(), show, no, 10000, "AVAILABLE"
        )));
        when(lock.tryLock(anyString(), anyLong())).thenReturn(true);
        when(resQ.existsActiveHold(eq(show), eq(no), any())).thenReturn(true);

        assertThrows(ReserveSeatService.ConflictException.class,
                () -> sut.reserve(new ReserveSeatCommand(user, show, no)));
    }
}
