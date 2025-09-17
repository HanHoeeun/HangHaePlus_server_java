package kr.hhplus.be.server.clean.queue.domain.entity;

import kr.hhplus.be.server.domain.enums.QueueStatus;

import java.util.UUID;

public class QueueToken {
    private final String token;
    private final UUID userId;
    private QueueStatus status;
    private int position;

    public QueueToken(UUID userId, int position, QueueStatus status) {
        this.token = "qtk_" + UUID.randomUUID();
        this.userId = userId;
        this.position = position;
        this.status = status;
    }

    public String getToken() { return token; }
    public UUID getUserId() { return userId; }
    public QueueStatus getStatus() { return status; }
    public int getPosition() { return position; }

    public void activate() {
        this.status = QueueStatus.ACTIVE;
        this.position = 0;
    }

    public void moveUp(int newPosition) {
        this.position = newPosition;
    }
}
