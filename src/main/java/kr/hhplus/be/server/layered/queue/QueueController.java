package kr.hhplus.be.server.layered.queue;

import kr.hhplus.be.server.layered.queue.dto.QueueIssueResponse;
import kr.hhplus.be.server.layered.queue.dto.QueueStatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    private final QueueService service;

    public QueueController(QueueService service) { this.service = service; }

    @PostMapping("/tokens")
    @ResponseStatus(HttpStatus.CREATED)
    public QueueIssueResponse issue(@RequestHeader("X-User-Id") String userId) {
        return service.issue(userId);
    }

    @GetMapping("/status")
    public QueueStatusResponse status(@RequestHeader("X-Queue-Token") String token) {
        return service.status(token);
    }
}
