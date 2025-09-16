package kr.hhplus.be.server.layered.catalog.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "concert_schedule")
public class ConcertSchedule {
    @Id
    @Column(name = "schedule_id")
    private UUID id;

    @Column(name = "concert_id", nullable = false)
    private UUID concertId;

    @Column(name = "show_at", nullable = false)
    private Instant showAt;

    // getter
    public UUID getId() { return id; }
    public UUID getConcertId() { return concertId; }
    public Instant getShowAt() { return showAt; }
}
