package kr.hhplus.be.server.layered.queue;

import kr.hhplus.be.server.layered.queue.dto.QueueIssueResponse;
import kr.hhplus.be.server.layered.queue.dto.QueueStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QueueService {
    private final QueueStore store;
    private final Clock clock;
    private final int activeTtlSeconds;
    private final int promotePerSec;   // ETA 계산용

    public QueueService(
            QueueStore store,
            Clock clock,
            @Value("${queue.active-ttl-seconds:600}") int activeTtlSeconds,
            @Value("${queue.promote-per-sec:100}") int promotePerSec) {
        this.store            = store;
        this.clock            = clock;
        this.activeTtlSeconds = activeTtlSeconds;
        this.promotePerSec    = Math.max(1, promotePerSec);
    }

    public QueueIssueResponse issue(String userId) {
        // 1) 기존 토큰 확인
        var existed = store.getUserToken(userId).orElse(null);
        if (existed != null) {
            if (store.isActive(existed)) {
                return new QueueIssueResponse(existed, "ACTIVE", null, null);
            }
            var rank = store.rankInWaiting(existed);
            if (rank != null) {
                return new QueueIssueResponse(existed, "WAITING", (int)(rank + 1), estimateEta(rank));
            }
            // 토큰 존재하지만 어디에도 없으면 새로 발급
        }

        // 2) 새 토큰 발급 (WAITING)
        var token = "qtk_" + UUID.randomUUID();
        var now = Instant.now(clock).toEpochMilli();

        store.setUserToken(userId, token);
        Map<String,String> f = new HashMap<>();
        f.put("userId", userId);
        f.put("status", "WAITING");
        f.put("createdAt", String.valueOf(now));
        store.putTokenHash(token, f);
        store.addWaiting(token, now);

        var rank = store.rankInWaiting(token);
        return new QueueIssueResponse(token, "WAITING", rank == null ? 1 : (int)(rank + 1), estimateEta(rank));
    }

    public QueueStatusResponse status(String token) {
        if (store.isActive(token)) {
            Integer ttl = store.activeTtlSeconds(token);
            return new QueueStatusResponse("ACTIVE", null, null, ttl);
        }
        if (!store.hasTokenHash(token)) {
            return new QueueStatusResponse("EXPIRED", null, null, null);
        }
        var rank = store.rankInWaiting(token);
        if (rank == null) {
            // 해시만 남고 대기열/액티브에 없으면 만료로 간주
            return new QueueStatusResponse("EXPIRED", null, null, null);
        }
        return new QueueStatusResponse("WAITING", (int)(rank + 1), estimateEta(rank), null);
    }

    private Integer estimateEta(Long rank0) {
        if (rank0 == null) return null;
        long ahead = rank0; // 0-based
        long seconds = Math.max(0, ahead / promotePerSec);
        return (int)seconds;
    }
}
