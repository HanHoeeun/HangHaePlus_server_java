package kr.hhplus.be.server.layered.queue.dto;

public record QueueIssueResponse (
        String  token,
        String  status,    // waiting | active
        Integer position,  // waiting일 때
        Integer etaSeconds
){

}
