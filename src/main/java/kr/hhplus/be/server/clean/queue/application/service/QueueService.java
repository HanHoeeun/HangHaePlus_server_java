package kr.hhplus.be.server.clean.queue.application.service;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;
import kr.hhplus.be.server.clean.queue.domain.entity.QueueToken;
import kr.hhplus.be.server.clean.queue.port.in.QueueUseCase;
import kr.hhplus.be.server.clean.queue.port.out.QueueStorePort;
import kr.hhplus.be.server.domain.enums.QueueStatus;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class QueueService implements QueueUseCase {

    private final QueueStorePort storePort;

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
}
