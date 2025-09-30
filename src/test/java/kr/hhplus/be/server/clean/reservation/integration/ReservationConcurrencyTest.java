package kr.hhplus.be.server.clean.reservation.integration;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationConcurrencyTest {

    @Autowired
    private ReserveSeatUseCase reserveSeatUseCase;

    @Autowired
    private SeatRepositoryPort seatRepositoryPort;

    @Test
    void only_one_user_can_reserve_the_same_seat_concurrently() throws Exception {
        // given: 테스트용 좌석 1개 생성 후 저장
        UUID seatId = UUID.randomUUID();
        int seatNumber = 1;
        long price = 50_000L;

        Seat seat = new Seat(seatId, seatNumber, SeatStatus.AVAILABLE, price);
        seatRepositoryPort.save(seat);

        UUID showId = UUID.randomUUID();

        // when: 동시에 두 명이 같은 좌석 예약 시도
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Future<String> result1 = executor.submit(() -> {
            try {
                var res = reserveSeatUseCase.reserve(
                        new ReserveSeatCommand(UUID.randomUUID(), showId, seatId, seatNumber)
                );
                return res.status(); // "HOLD" or "FAIL"
            } catch (Exception e) {
                return "FAIL";
            } finally {
                latch.countDown();
            }
        });

        Future<String> result2 = executor.submit(() -> {
            try {
                var res = reserveSeatUseCase.reserve(
                        new ReserveSeatCommand(UUID.randomUUID(), showId, seatId, seatNumber)
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

        // then: 하나는 HOLD, 다른 하나는 FAIL 이어야 함
        String status1 = result1.get();
        String status2 = result2.get();

        assertThat(List.of(status1, status2))
                .containsExactlyInAnyOrder("HOLD", "FAIL");
    }

    @Test
    void only_one_out_of_ten_users_can_reserve_the_same_seat() throws Exception {
        // given
        UUID seatId = UUID.randomUUID();
        int seatNumber = 1;
        long price = 50_000L;
        UUID showId = UUID.randomUUID();

        seatRepositoryPort.save(new Seat(seatId, seatNumber, SeatStatus.AVAILABLE, price));

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        ConcurrentLinkedQueue<String> results = new ConcurrentLinkedQueue<>();

        IntStream.range(0, 10).forEach(i -> executor.submit(() -> {
            try {
                var result = reserveSeatUseCase.reserve(
                        new ReserveSeatCommand(UUID.randomUUID(), showId, seatId, seatNumber)
                );
                results.add(result.status()); // ex) "HOLD"
            } catch (Exception e) {
                results.add("FAIL");
            } finally {
                latch.countDown();
            }
        }));

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertThat(completed).isTrue(); // 시간 내에 10개 스레드가 모두 실행 완료되었는지 검증

        executor.shutdown();

        long holdCount = results.stream().filter("HOLD"::equals).count();
        long failCount = results.stream().filter("FAIL"::equals).count();

        // then
        assertThat(holdCount).isEqualTo(1); // 딱 1명만 HOLD 성공
        assertThat(holdCount + failCount).isEqualTo(10); // 총 10명 시도
    }
}
