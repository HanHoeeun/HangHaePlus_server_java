package kr.hhplus.be.server.clean.queue.adapter.in.web;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;
import kr.hhplus.be.server.clean.queue.port.in.QueueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueUseCase queueUseCase;

    @PostMapping("/tokens")
    public QueueIssueResult issueToken(@RequestParam UUID userId) {
        return queueUseCase.issueToken(userId);
    }

    @GetMapping("/status")
    public QueueStatusResult checkStatus(@RequestHeader("X-Queue-Token") String token) {
        return queueUseCase.checkStatus(token);
    }
}
