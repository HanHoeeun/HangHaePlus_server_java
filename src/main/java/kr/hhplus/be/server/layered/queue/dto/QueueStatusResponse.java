package kr.hhplus.be.server.layered.queue.dto;

public record QueueStatusResponse(
        String  status,          // waiting | active | expired
        Integer position,        // waiting일 때
        Integer etaSeconds,      // waiting일 때
        Integer activeTtlSeconds // active일 때
) {}
