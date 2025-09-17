package kr.hhplus.be.server.clean.queue.adapter.in.web.response;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;

public record QueueIssueResponse(
        String token,
        int position,
        String status
) {
    public static QueueIssueResponse from(QueueIssueResult result) {
        return new QueueIssueResponse(result.token(), result.position(), result.status());
    }
}
