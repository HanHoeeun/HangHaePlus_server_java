package kr.hhplus.be.server.clean.queue.adapter.in.web.response;

import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;

public record QueueStatusResponse(
        int position,
        String status
) {
    public static QueueStatusResponse from(QueueStatusResult result) {
        return new QueueStatusResponse(result.position(), result.status());
    }
}
