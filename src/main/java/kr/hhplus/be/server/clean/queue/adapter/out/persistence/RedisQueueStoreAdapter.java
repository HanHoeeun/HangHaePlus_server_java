package kr.hhplus.be.server.clean.queue.adapter.out.persistence;

import kr.hhplus.be.server.clean.queue.domain.entity.QueueToken;
import kr.hhplus.be.server.clean.queue.port.out.QueueStorePort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RedisQueueStoreAdapter implements QueueStorePort {

    // ⚠️ 실제 Redis 대신 메모리 Map 예시 (테스트용)
    private final Map<String, QueueToken> store = new LinkedHashMap<>();

    @Override
    public QueueToken save(QueueToken token) {
        store.put(token.getToken(), token);
        return token;
    }

    @Override
    public Optional<QueueToken> findByToken(String token) {
        return Optional.ofNullable(store.get(token));
    }

    @Override
    public int countWaiting() {
        return (int) store.values().stream()
                .filter(t -> t.getStatus().name().equals("WAITING"))
                .count();
    }
}
