package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.dto.ShowAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.persistence.*;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import kr.hhplus.be.server.domain.enums.ResevationStatus;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogQueryServiceTest {

    ConcertScheduleRepository scheduleRepo = mock(ConcertScheduleRepository.class);
    SeatRepository seatRepo = mock(SeatRepository.class);
    ReservationRepository reservationRepo = mock(ReservationRepository.class);
    Clock clock = Clock.fixed(Instant.parse("2025-09-03T12:00:00Z"), ZoneOffset.UTC);

    CatalogQueryService sut = new CatalogQueryService(scheduleRepo, seatRepo, reservationRepo, clock);

    @Test
    void show_availability_counts_out_available_excluding_sold_and_active_holds() {
        UUID concert = UUID.randomUUID();
        UUID schedule = UUID.randomUUID();

        ConcertSchedule cs = mock(ConcertSchedule.class);
        when(cs.getId()).thenReturn(schedule);
        when(cs.getConcertId()).thenReturn(concert);
        when(cs.getShowAt()).thenReturn(Instant.parse("2025-09-10T10:00:00Z"));
        when(scheduleRepo.findByConcertIdOrderByShowAtAsc(concert)).thenReturn(List.of(cs));

        Seat s1 = mockSeat(schedule, 1, 10000, SeatStatus.AVAILABLE);
        Seat s2 = mockSeat(schedule, 2, 10000, SeatStatus.SOLD);
        Seat s3 = mockSeat(schedule, 3, 10000, SeatStatus.AVAILABLE);
        when(seatRepo.findByScheduleIdOrderBySeatNumberAsc(schedule)).thenReturn(List.of(s1, s2, s3));

        Reservation rHoldOn3 = mockReservation(schedule, 3, ResevationStatus.HELD,
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                Instant.parse("2025-09-03T12:05:00Z")); // active
        when(reservationRepo.findActiveHolds(eq(schedule), any())).thenReturn(List.of(rHoldOn3));

        List<ShowAvailabilityDto> res = sut.listShowsWithAvailability(concert);

        assertEquals(1, res.size());
        ShowAvailabilityDto dto = res.get(0);
        assertEquals(3, dto.totalSeats());
        assertEquals(1, dto.availableSeats()); // s1만 true (s2 SOLD, s3 HELD active)
    }

    @Test
    void seat_availability_labels_self_vs_others_and_sold() {
        UUID schedule = UUID.randomUUID();
        UUID self = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        Seat s1 = mockSeat(schedule, 1, 10000, SeatStatus.AVAILABLE);
        Seat s2 = mockSeat(schedule, 2, 10000, SeatStatus.AVAILABLE);
        Seat s3 = mockSeat(schedule, 3, 10000, SeatStatus.SOLD);
        when(seatRepo.findByScheduleIdOrderBySeatNumberAsc(schedule)).thenReturn(List.of(s1, s2, s3));

        Reservation holdBySelf = mockReservation(schedule, 1, ResevationStatus.HELD, self,
                Instant.parse("2025-09-03T12:05:00Z"));
        Reservation holdByOther = mockReservation(schedule, 2, ResevationStatus.HELD,
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                Instant.parse("2025-09-03T12:05:00Z"));
        when(reservationRepo.findActiveHolds(eq(schedule), any())).thenReturn(List.of(holdBySelf, holdByOther));

        List<SeatAvailabilityDto> out = sut.listSeatAvailability(schedule, self);

        assertEquals(3, out.size());
        assertEquals("HELD_BY_SELF", out.get(0).label());
        assertEquals("HELD_BY_OTHERS", out.get(1).label());
        assertEquals("SOLD", out.get(2).label());
    }

    // helpers
    private Seat mockSeat(UUID scheduleId, int no, long price, SeatStatus status) {
        Seat s = mock(Seat.class);
        when(s.getScheduleId()).thenReturn(scheduleId);
        when(s.getSeatNumber()).thenReturn(no);
        when(s.getPrice()).thenReturn(price);
        when(s.getStatus()).thenReturn(status);
        return s;
    }

    private Reservation mockReservation(UUID scheduleId, int no, ResevationStatus st, UUID userId, Instant exp) {
        Reservation r = mock(Reservation.class);
        when(r.getScheduleId()).thenReturn(scheduleId);
        when(r.getSeatNumber()).thenReturn(no);
        when(r.getStatus()).thenReturn(st);
        when(r.getUserId()).thenReturn(userId);
        when(r.getHoldExpiresAt()).thenReturn(exp);
        return r;
    }
}
