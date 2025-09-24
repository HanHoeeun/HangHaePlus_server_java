package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationConcurrencyTest {

    @Autowired
    private ReserveSeatUseCase reserveSeatUseCase;

    @Test
    void concurrent_seat_reservation_conflict_test() throws Exception {
        UUID showId = UUID.randomUUID();
        int seatNumber = 1;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Future<String> result1 = executor.submit(() -> {
            try {
                var res = reserveSeatUseCase.reserve(
                        new ReserveSeatCommand(UUID.randomUUID(), showId, UUID.randomUUID(), seatNumber)
                );

                return res.status(); // HOLD
            } finally {
                latch.countDown();
            }
        });

        Future<String> result2 = executor.submit(() -> {
            try {
                var res = reserveSeatUseCase.reserve(
                        new ReserveSeatCommand(UUID.randomUUID(), showId, UUID.randomUUID(), seatNumber)
                );

                return res.status();
            } catch (Exception e) {
                return "FAIL";
            } finally {
                latch.countDown();
            }
        });

        latch.await();
        executor.shutdown();

        String status1 = result1.get();
        String status2 = result2.get();

        assertThat(status1).isIn("HOLD", "FAIL");
        assertThat(status2).isIn("HOLD", "FAIL");
        assertThat(status1).isNotEqualTo(status2); // only one succeeds
    }
}
