package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.domain.enums.SeatStatus;
import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.dto.ShowAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.persistence.ConcertScheduleRepository;
import kr.hhplus.be.server.layered.catalog.persistence.SeatRepository;
import kr.hhplus.be.server.layered.catalog.persistence.ReservationRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @InjectMocks
    private CatalogService catalogService;

    @Mock
    private ConcertScheduleRepository scheduleRepo;

    @Mock
    private SeatRepository seatRepo;

    @Mock
    private ReservationRepository reservationRepo;

    private final Clock fixedClock = Clock.fixed(Instant.parse("2025-09-17T12:00:00Z"), ZoneId.of("UTC"));

    @Test
    @DisplayName("공연 회차별 예약 가능 좌석 수 요약 조회 성공")
    void listShowsWithAvailability_success() {
        // given
        UUID concertId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();

        var show = new ConcertSchedule(scheduleId, concertId, Instant.now(fixedClock).plusSeconds(3600));
        given(scheduleRepo.findByConcertIdOrderByShowAtAsc(concertId)).willReturn(List.of(show));

        var seat1 = new Seat(scheduleId, 1, 10000, SeatStatus.AVAILABLE);
        var seat2 = new Seat(scheduleId, 2, 10000, SeatStatus.SOLD);
        given(seatRepo.findByScheduleIdOrderBySeatNumberAsc(scheduleId)).willReturn(List.of(seat1, seat2));

        given(reservationRepo.findActiveHolds(scheduleId, Instant.now(fixedClock))).willReturn(List.of());

        // when
        List<ShowAvailabilityDto> result = catalogService.listShowsWithAvailability(concertId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).availableSeats()).isEqualTo(1);
        assertThat(result.get(0).totalSeats()).isEqualTo(2);
    }

    @Test
    @DisplayName("특정 회차의 좌석 상세 가용성 조회 성공")
    void listSeatAvailability_success() {
        // given
        UUID scheduleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var seat1 = new Seat(scheduleId, 1, 10000, SeatStatus.AVAILABLE);
        var seat2 = new Seat(scheduleId, 2, 10000, SeatStatus.SOLD);
        given(seatRepo.findByScheduleIdOrderBySeatNumberAsc(scheduleId)).willReturn(List.of(seat1, seat2));

        given(reservationRepo.findActiveHolds(scheduleId, Instant.now(fixedClock))).willReturn(List.of());

        // when
        List<SeatAvailabilityDto> result = catalogService.listSeatAvailability(scheduleId, userId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).label()).isEqualTo("AVAILABLE");
        assertThat(result.get(1).label()).isEqualTo("SOLD");
    }
}
