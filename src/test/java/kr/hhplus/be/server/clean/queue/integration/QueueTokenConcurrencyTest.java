package kr.hhplus.be.server.clean.queue.integration;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.port.in.QueueUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class QueueTokenConcurrencyTest {

    @Autowired
    private QueueUseCase queueUseCase;

    @Test
    void only_one_token_should_be_issued_for_same_user_concurrently() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        ConcurrentLinkedQueue<QueueIssueResult> results = new ConcurrentLinkedQueue<>();

        // when: 동시에 10개의 요청 발행
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    QueueIssueResult result = queueUseCase.issueToken(userId);
                    results.add(result);
                } catch (Exception ignored) {
                    // 중복 발급 막힐 수도 있음
                } finally {
                    latch.countDown();
                }
            });
        }

        // then
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        executor.shutdown();

        // 중복 제거 후 토큰 수 확인
        long uniqueTokenCount = results.stream()
                .map(QueueIssueResult::token)
                .distinct()
                .count();

        assertThat(uniqueTokenCount)
                .withFailMessage("동일 유저에 대해 여러 토큰이 발급되었습니다")
                .isEqualTo(1);

        // 응답 개수 확인 (성공한 쓰레드 수)
        assertThat(results).isNotEmpty();
    }
}
