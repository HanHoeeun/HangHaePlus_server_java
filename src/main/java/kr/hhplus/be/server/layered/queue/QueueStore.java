package kr.hhplus.be.server.layered.queue;

import java.util.Map;
import java.util.Optional;

public interface QueueStore {
    Optional<String> getUserToken(String userId);
    void setUserToken(String userId, String token);

    // 동시 발급 제어
    boolean setUserTokenIfAbsent(String userId, String token);

    void addWaiting(String token, long scoreMillis);
    Long rankInWaiting(String token);             // 0-based rank (null if not exists)
    boolean removeFromWaiting(String token);      // true if removed

    void putTokenHash(String token, Map<String,String> fields);
    boolean hasTokenHash(String token);

    // 해시 TTL/삭제/조회
    void expireTokenHash(String token, int seconds);
    void deleteTokenHash(String token);
    Optional<String> getTokenHashField(String token, String field);

    boolean isActive(String token);
    void setActive(String token, String userId, int ttlSeconds);

    Integer activeTtlSeconds(String token);       // null if not active

    // 사용자 pointer 삭제
    void deleteUserToken(String userId);
}
