package kr.hhplus.be.server.layered.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

@Component
public class QueuePromoter {
    private final QueueStore store;
    private final LockOps lock; // 분산락(예: Redis SETNX key=queue:promoter:lock, TTL=3s)
    private final int promotePerSec;
    private final int activeTtlSeconds;
    private final int maxActive; // 활성 슬롯 N

    public QueuePromoter(QueueStore store, LockOps lock,
                         @Value("${queue.promote-per-sec:100}") int promotePerSec,
                         @Value("${queue.active-ttl-seconds:600}") int activeTtlSeconds,
                         @Value("${queue.max-active:1000}") int maxActive) {
        this.store = store;
        this.lock = lock;
        this.promotePerSec = promotePerSec;
        this.activeTtlSeconds = activeTtlSeconds;
        this.maxActive = maxActive;
    }

    // 매 초마다 실행
    @Scheduled(fixedRate = 1000)
    public void promoteTick() {
        if (!lock.tryLock("queue:promoter:lock", Duration.ofSeconds(3))) return;
        try {
            int activatable = Math.max(0, maxActive - countActiveApprox());
            int quota = Math.min(promotePerSec, activatable);
            if (quota <= 0) return;

            // 대기열에서 가장 오래된 토큰부터 quota만큼 꺼내 승격

        } finally {
            lock.unlock("queue:promoter:lock");
        }
    }

    private int countActiveApprox() {
        return 0;
    }

    public interface LockOps {
        boolean tryLock(String key, Duration ttl);
        void unlock(String key);
    }
}
