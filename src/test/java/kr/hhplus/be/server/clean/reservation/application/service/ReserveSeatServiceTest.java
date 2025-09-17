package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.clean.reservation.port.out.SeatLockPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)   // ✅ 추가
class ReserveSeatServiceTest {

    @InjectMocks
    private ReserveSeatService reserveSeatService;

    @Mock
    private SeatRepositoryPort seatRepository;

    @Mock
    private SeatLockPort seatLockPort;

    private final UUID seatId = UUID.randomUUID();
    private final Seat seat = new Seat(seatId, SeatStatus.AVAILABLE, 10000);

    @Test
    @DisplayName("좌석 예약 성공 → AVAILABLE → HOLD 상태 변경")
    void reserveSeat_success() {
        // given
        given(seatRepository.findById(seatId)).willReturn(Optional.of(seat));
        given(seatRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        ReserveSeatResult result = reserveSeatService.reserve(new ReserveSeatCommand(seatId, UUID.randomUUID()));

        // then
        assertThat(result.getSeatStatus()).isEqualTo("HOLD");
        verify(seatLockPort, times(1)).lock(seatId);
        verify(seatRepository, times(1)).save(seat);
    }

    @Test
    @DisplayName("이미 HOLD 좌석 예약 시 → 예외 발생")
    void reserveSeat_fail_alreadyHold() {
        // given
        seat.hold(); // 상태 변경
        given(seatRepository.findById(seatId)).willReturn(Optional.of(seat));

        // when & then
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> reserveSeatService.reserve(new ReserveSeatCommand(seatId, UUID.randomUUID()))
        );
    }
}
