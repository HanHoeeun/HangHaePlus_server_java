package kr.hhplus.be.server.clean.queue.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueuePromotionJob {

    private final QueueService queueService;

    /**
     * 10초마다 WAITING → ACTIVE 승격
     */
    @Scheduled(fixedDelay = 10_000)
    public void promote() {   // ✅ 인자 없음
        queueService.promoteWaitingTokens();
    }
}
