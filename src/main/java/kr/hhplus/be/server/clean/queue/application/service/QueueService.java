package kr.hhplus.be.server.clean.queue.application.service;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;
import kr.hhplus.be.server.clean.queue.domain.entity.QueueToken;
import kr.hhplus.be.server.clean.queue.port.in.QueueUseCase;
import kr.hhplus.be.server.clean.queue.port.out.QueueStorePort;
import kr.hhplus.be.server.domain.enums.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QueueService implements QueueUseCase {

    private final QueueStorePort storePort;

    // 동시 허용 가능한 ACTIVE 토큰 수
    private final int activeLimit = 1000;

    @Override
    public QueueIssueResult issueToken(UUID userId) {
        int position = storePort.countWaiting() + 1;
        QueueToken token = new QueueToken(userId, position, QueueStatus.WAITING);
        storePort.save(token);
        return new QueueIssueResult(token.getToken(), token.getPosition(), token.getStatus().name());
    }

    @Override
    public QueueStatusResult checkStatus(String tokenStr) {
        QueueToken token = storePort.findByToken(tokenStr)
                .orElseThrow(() -> new IllegalArgumentException("토큰 없음"));
        return new QueueStatusResult(token.getPosition(), token.getStatus().name());
    }

    /**
     * 주기적으로 WAITING → ACTIVE 승격
     * (예: 10초마다 실행)
     */
    @Scheduled(fixedDelay = 10_000)
    public void promoteWaitingTokens() {
        long activeCount = storePort.countActive();
        if (activeCount >= activeLimit) {
            return; // 이미 가득 참
        }

        int slots = (int) (activeLimit - activeCount);
        List<QueueToken> waitingList = storePort.findOldestWaiting(slots);

        for (QueueToken token : waitingList) {
            token.activate();
            storePort.save(token);
        }
    }
}
