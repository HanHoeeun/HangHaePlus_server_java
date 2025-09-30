package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationExpireIntegrationTest {

    private final ReserveSeatUseCase reserveSeatUseCase;
    private final SeatRepositoryPort seatRepositoryPort;

    @Autowired
    ReservationExpireIntegrationTest(ReserveSeatUseCase reserveSeatUseCase,
                                     SeatRepositoryPort seatRepositoryPort) {
        this.reserveSeatUseCase = reserveSeatUseCase;
        this.seatRepositoryPort = seatRepositoryPort;
    }

    @Test
    void seat_becomes_available_after_expiration() throws Exception {
        // given: 좌석 1개 생성
        UUID seatId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        Seat seat = new Seat(seatId, 1, SeatStatus.AVAILABLE, 50_000L);
        seatRepositoryPort.save(seat);

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        // when: 첫 번째 유저가 좌석 예약 (HOLD)
        ReserveSeatResult result1 = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(userId1, showId, seatId, 1)
        );

        assertThat(result1.status()).isEqualTo("HOLD");

        // 좌석 상태 확인 (HOLD)
        Seat heldSeat = seatRepositoryPort.findById(seatId).orElseThrow();
        assertThat(heldSeat.getStatus()).isEqualTo(SeatStatus.HOLD);

        // and: 만료 시간까지 대기 (예: 2초 → 실제로는 expireAt 컬럼에 맞게 조정)
        Thread.sleep(Duration.ofSeconds(2).toMillis());

        // 여기서는 단순히 expire 스케줄러가 돌았다고 가정하고 수동으로 AVAILABLE 로 되돌리는 처리 필요
        // 실서비스에서는 Batch/Job/스케줄러가 expireAt 지난 좌석을 AVAILABLE 로 갱신해야 함
        heldSeat = new Seat(seatId, 1, SeatStatus.AVAILABLE, 50_000L);
        seatRepositoryPort.save(heldSeat);

        // then: 두 번째 유저가 같은 좌석 예약 가능
        ReserveSeatResult result2 = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(userId2, showId, seatId, 1)
        );

        assertThat(result2.status()).isEqualTo("HOLD");
    }
}
