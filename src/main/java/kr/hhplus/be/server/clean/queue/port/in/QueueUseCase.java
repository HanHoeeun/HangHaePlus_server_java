package kr.hhplus.be.server.clean.queue.port.in;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;

import java.util.UUID;

public interface QueueUseCase {
    QueueIssueResult issueToken(UUID userId);
    QueueStatusResult checkStatus(String token);
}
