package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.queue.port.in.QueueUseCase;
import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.port.in.WalletUseCase;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationFlowIntegrationTest {

    private final QueueUseCase queueUseCase;
    private final ReserveSeatUseCase reserveSeatUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final WalletUseCase walletUseCase;
    private final SeatRepositoryPort seatRepositoryPort;

    @Autowired
    ReservationFlowIntegrationTest(QueueUseCase queueUseCase,
                                   ReserveSeatUseCase reserveSeatUseCase,
                                   ConfirmReservationUseCase confirmReservationUseCase,
                                   WalletUseCase walletUseCase,
                                   SeatRepositoryPort seatRepositoryPort) {
        this.queueUseCase = queueUseCase;
        this.reserveSeatUseCase = reserveSeatUseCase;
        this.confirmReservationUseCase = confirmReservationUseCase;
        this.walletUseCase = walletUseCase;
        this.seatRepositoryPort = seatRepositoryPort;
    }

    @Test
    void user_flow_from_token_to_reservation_and_payment() {
        // given: 유저 생성 및 지갑 충전
        UUID userId = UUID.randomUUID();
        walletUseCase.charge(new WalletChargeCommand(userId, 100_000));

        // 공연 & 좌석 준비
        UUID showId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        Seat seat = new Seat(seatId, 1, SeatStatus.AVAILABLE, 50_000L);
        seatRepositoryPort.save(seat);

        // when: 대기열 토큰 발급
        queueUseCase.issueToken(userId);

        // and: 좌석 예약
        ReserveSeatResult reserved = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(userId, showId, seatId, 1)
        );

        // and: 예약 확정 및 결제
        ConfirmReservationCommand confirmCmd =
                new ConfirmReservationCommand(userId, reserved.reservationId(), 50_000);

        var confirmed = confirmReservationUseCase.confirm(confirmCmd);

        // then: 최종 잔액 확인
        WalletBalanceResult balance = walletUseCase.getBalance(userId);
        assertThat(balance.balance()).isEqualTo(50_000);

        // 예약이 CONFIRMED 상태인지 확인
        assertThat(confirmed.getStatus().name()).isEqualTo("CONFIRMED");
    }
}
