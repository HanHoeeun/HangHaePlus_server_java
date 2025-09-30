package kr.hhplus.be.server.clean.queue.port.out;

import kr.hhplus.be.server.clean.queue.domain.entity.QueueToken;

import java.util.Optional;

public interface QueueStorePort {
    QueueToken save(QueueToken token);
    Optional<QueueToken> findByToken(String token);
    int countWaiting();
}
