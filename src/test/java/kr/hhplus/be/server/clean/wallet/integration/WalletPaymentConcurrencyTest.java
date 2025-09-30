package kr.hhplus.be.server.clean.wallet.integration;

import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.port.in.WalletUseCase;
import kr.hhplus.be.server.clean.wallet.port.out.WalletRepositoryPort;
import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WalletPaymentConcurrencyTest {

    @Autowired
    private WalletUseCase walletUseCase;

    @Autowired
    private WalletRepositoryPort walletRepositoryPort;

    @Test
    void wallet_balance_should_not_go_negative_under_concurrent_payments() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        walletUseCase.charge(new WalletChargeCommand(userId, 10_000)); // 1만원 충전

        int numThreads = 10;
        long requestAmount = 3_000; // 각 요청당 3000원 결제 시도
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger();

        // when
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    Wallet wallet = walletRepositoryPort.findByUserId(userId).orElseThrow();
                    if (wallet.canPay(requestAmount)) {
                        wallet.deduct(requestAmount);
                        walletRepositoryPort.save(wallet);
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        executor.shutdown();

        // then
        WalletBalanceResult result = walletUseCase.getBalance(UUID.fromString(userId.toString()));

        assertThat(result.balance()).isGreaterThanOrEqualTo(0L);
        assertThat(successCount.get()).isLessThanOrEqualTo(3); // 최대 3건까지 성공 가능 (3000*3 <= 10,000)
    }
}
