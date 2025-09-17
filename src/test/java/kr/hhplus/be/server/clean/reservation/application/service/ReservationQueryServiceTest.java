package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.AvailableDatesResult;
import kr.hhplus.be.server.clean.reservation.application.dto.AvailableSeatsResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.ScheduleRepositoryPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationQueryServiceTest {

    @InjectMocks
    private ReservationQueryService reservationQueryService;

    @Mock
    private ScheduleRepositoryPort scheduleRepository;

    @Test
    @DisplayName("예약 가능 날짜 조회 성공")
    void getAvailableDates_success() {
        // given
        LocalDate date1 = LocalDate.of(2025, 9, 20);
        LocalDate date2 = LocalDate.of(2025, 9, 21);
        given(scheduleRepository.findAvailableDates()).willReturn(List.of(date1, date2));

        // when
        List<AvailableDatesResult> result = reservationQueryService.getAvailableDates();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).date()).isEqualTo(date1);
        assertThat(result.get(1).date()).isEqualTo(date2);
    }

    @Test
    @DisplayName("예약 가능 좌석 조회 성공")
    void getAvailableSeats_success() {
        // given
        UUID scheduleId = UUID.randomUUID();
        Seat seat1 = new Seat(UUID.randomUUID(), 1, SeatStatus.AVAILABLE, 10000);
        Seat seat2 = new Seat(UUID.randomUUID(), 2, SeatStatus.SOLD, 12000);

        given(scheduleRepository.findSeatsBySchedule(scheduleId)).willReturn(List.of(seat1, seat2));

        // when
        List<AvailableSeatsResult> result = reservationQueryService.getAvailableSeats(scheduleId);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).seatNumber()).isEqualTo(1);
        assertThat(result.get(0).status()).isEqualTo("AVAILABLE");
        assertThat(result.get(1).seatNumber()).isEqualTo(2);
        assertThat(result.get(1).status()).isEqualTo("SOLD");
    }
}
