package kr.hhplus.be.server.layered.queue;

import java.util.Map;
import java.util.Optional;

public interface QueueStore {
    // 사용자→토큰 포인터
    Optional<String> getUserToken(String userId);
    void setUserToken(String userId, String token);
    boolean setUserTokenIfAbsent(String userId, String token); // CAS (SETNX)
    void deleteUserToken(String userId);                        // 포인터 삭제

    // 대기열 ZSET
    void addWaiting(String token, long scoreMillis);
    Long rankInWaiting(String token);               // 0-based rank, null if not exists
    boolean removeFromWaiting(String token);

    // 토큰 해시 (qt:{token})
    void putTokenHash(String token, Map<String,String> fields);
    boolean hasTokenHash(String token);
    void expireTokenHash(String token, int seconds);
    void deleteTokenHash(String token);
    Optional<String> getTokenHashField(String token, String field);

    // ACTIVE 키
    boolean isActive(String token);
    void setActive(String token, String userId, int ttlSeconds);
    Integer activeTtlSeconds(String token);
}
