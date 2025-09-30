package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.port.in.WalletUseCase;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationPaymentIdempotencyTest {

    @Autowired
    private ReserveSeatUseCase reserveSeatUseCase;

    @Autowired
    private ConfirmReservationUseCase confirmReservationUseCase;

    @Autowired
    private WalletUseCase walletUseCase;

    @Autowired
    private SeatRepositoryPort seatRepositoryPort;

    @Test
    void payment_should_be_idempotent_for_duplicate_requests() throws Exception {
        // given: 유저, 좌석, 예약 생성
        UUID userId = UUID.randomUUID();
        UUID showId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();
        int seatNumber = 1;
        long price = 50_000L;

        walletUseCase.charge(new WalletChargeCommand(userId, 100_000));

        Seat seat = new Seat(seatId, seatNumber, SeatStatus.AVAILABLE, price);
        seatRepositoryPort.save(seat);

        ReserveSeatResult reserve = reserveSeatUseCase.reserve(
                new ReserveSeatCommand(userId, showId, seatId, seatNumber)
        );

        UUID reservationId = reserve.reservationId();
        ConfirmReservationCommand command = new ConfirmReservationCommand(userId, reservationId, price);

        // when: 동시에 두 번 confirm 호출
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                try {
                    confirmReservationUseCase.confirm(command);
                    results.add("SUCCESS");
                } catch (Exception e) {
                    results.add("FAIL");
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        executor.shutdown();

        // then: 하나만 성공, 나머지는 무시 or 예외
        long successCount = results.stream().filter(r -> r.equals("SUCCESS")).count();
        long failCount = results.stream().filter(r -> r.equals("FAIL")).count();

        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(1);

        // 그리고 잔액이 1번만 차감되었는지 검증
        WalletBalanceResult balance = walletUseCase.getBalance(userId);
        assertThat(balance.balance()).isEqualTo(50_000L);
    }
}
