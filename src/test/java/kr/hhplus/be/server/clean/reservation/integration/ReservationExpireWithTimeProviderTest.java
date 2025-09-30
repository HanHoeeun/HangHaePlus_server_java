package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.clean.reservation.port.out.TimeProvider;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
@Import(ReservationExpireWithTimeProviderTest.MockTimeProviderConfig.class)
class ReservationExpireWithTimeProviderTest {

    @Autowired
    private ReserveSeatUseCase reserveSeatUseCase;

    @Autowired
    private SeatRepositoryPort seatRepositoryPort;

    @Autowired
    private TimeProvider timeProvider;

    @Test
    void seat_becomes_available_after_expiration_using_timeProvider() {
        // given
        UUID seatId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        // 좌석 저장
        Seat seat = new Seat(seatId, 1, SeatStatus.AVAILABLE, 50_000L);
        seatRepositoryPort.save(seat);

        Instant now = Instant.now();
        given(timeProvider.now()).willReturn(now); // 현재 시간 고정

        // when: 첫 번째 유저가 좌석 HOLD
        ReserveSeatResult result1 = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(user1, showId, seatId, 1)
        );

        assertThat(result1.status()).isEqualTo("HOLD");

        // and: 만료 시간 이후로 시간 점프
        given(timeProvider.now()).willReturn(now.plusSeconds(600)); // 10분 후로 점프

        // then: 두 번째 유저가 동일 좌석 예약 가능
        ReserveSeatResult result2 = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(user2, showId, seatId, 1)
        );

        assertThat(result2.status()).isEqualTo("HOLD"); // 다시 예약 가능
    }

    @TestConfiguration
    static class MockTimeProviderConfig {
        @Bean
        public TimeProvider timeProvider() {
            return Mockito.mock(TimeProvider.class);
        }
    }
}
