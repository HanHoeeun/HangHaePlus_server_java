package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.TimeProvider;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class SeatExpirationIntegrationTest {

    @Autowired
    private ReserveSeatUseCase reserveSeatUseCase;

    @MockBean
    private TimeProvider timeProvider;

    @Test
    @DisplayName("만료 시간 이후 동일 좌석 재예약 가능 테스트")
    void seat_can_be_reserved_again_after_expiration() {
        // given
        UUID seatId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        Instant now = Instant.now();
        given(timeProvider.now()).willReturn(now); // 현재 시간 설정

        // when - 첫 번째 유저가 좌석 예약
        ReserveSeatCommand firstCommand = new ReserveSeatCommand(firstUserId, showId, seatId, 1);
        ReserveSeatResult firstResult = reserveSeatUseCase.reserve(firstCommand);

        assertThat(firstResult.status()).isEqualTo(SeatStatus.HOLD.name());

        // then - expireAt 시간 이후로 시간 점프
        given(timeProvider.now()).willReturn(now.plus(Duration.ofMinutes(11))); // 만료 10분 후

        // and - 두 번째 유저가 동일 좌석 예약 시도
        ReserveSeatCommand secondCommand = new ReserveSeatCommand(secondUserId, showId, seatId, 1);
        ReserveSeatResult secondResult = reserveSeatUseCase.reserve(secondCommand);

        assertThat(secondResult.status()).isEqualTo(SeatStatus.HOLD.name()); // 재예약 성공해야 함
    }
}
