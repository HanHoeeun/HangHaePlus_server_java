package kr.hhplus.be.server.clean.queue.application.service;

import kr.hhplus.be.server.clean.queue.port.out.QueueStorePort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueuePromotionJob {
    private final QueueService queueService;
    private final QueueStorePort queueStore;

    // 10초마다 실행
    @Scheduled(fixedRate = 10000)
    public void promoteWaitingTokens() {
        int activeLimit = 1000; // 동시에 허용할 활성 사용자 수
        queueService.promoteWaitingTokens(activeLimit);
    }
}
