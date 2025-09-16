package kr.hhplus.be.server.layered.queue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.be.server.layered.queue.QueueKeys.*;

@Repository
public class RedisQueueStore implements QueueStore {
    private final StringRedisTemplate redisTemplate;

    public RedisQueueStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ---- 사용자→토큰 포인터 ----
    @Override public Optional<String> getUserToken(String userId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(userToken(userId)));
    }

    @Override public void setUserToken(String userId, String token) {
        if (userId == null || token == null) throw new IllegalArgumentException("userId/token must not be null");
        redisTemplate.opsForValue().set(userToken(userId), token);
    }

    @Override public boolean setUserTokenIfAbsent(String userId, String token) {
        if (userId == null || token == null) throw new IllegalArgumentException("userId/token must not be null");
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(userToken(userId), token); // SETNX
        return Boolean.TRUE.equals(ok);
    }

    @Override public void deleteUserToken(String userId) {
        redisTemplate.delete(userToken(userId));
    }

    // ---- 대기열 ZSET ----
    @Override public void addWaiting(String token, long scoreMillis) {
        if (token == null) throw new IllegalArgumentException("token must not be null");
        redisTemplate.opsForZSet().add(waitingZset(), token, (double) scoreMillis);
    }

    @Override public Long rankInWaiting(String token) {
        return redisTemplate.opsForZSet().rank(waitingZset(), token);
    }

    @Override public boolean removeFromWaiting(String token) {
        Long removed = redisTemplate.opsForZSet().remove(waitingZset(), token);
        return removed != null && removed > 0;
    }

    // ---- 토큰 해시 ----
    @Override public void putTokenHash(String token, Map<String, String> fields) {
        if (token == null || fields == null) throw new IllegalArgumentException("token/fields must not be null");
        redisTemplate.opsForHash().putAll(tokenHash(token), fields);
        // 누적 방지 기본 TTL (24h)
        redisTemplate.expire(tokenHash(token), Duration.ofHours(24));
    }

    @Override public boolean hasTokenHash(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenHash(token)));
    }

    @Override public void expireTokenHash(String token, int seconds) {
        redisTemplate.expire(tokenHash(token), Duration.ofSeconds(seconds));
    }

    @Override public void deleteTokenHash(String token) {
        redisTemplate.delete(tokenHash(token));
    }

    @Override public Optional<String> getTokenHashField(String token, String field) {
        Object v = redisTemplate.opsForHash().get(tokenHash(token), field);
        return Optional.ofNullable(v == null ? null : v.toString());
    }

    // ---- ACTIVE ----
    @Override public boolean isActive(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(activeKey(token)));
    }

    @Override public void setActive(String token, String userId, int ttlSeconds) {
        if (token == null || userId == null) throw new IllegalArgumentException("token/userId must not be null");
        redisTemplate.opsForValue().set(activeKey(token), userId, Duration.ofSeconds(ttlSeconds));
    }

    @Override public Integer activeTtlSeconds(String token) {
        Long ttl = redisTemplate.getExpire(activeKey(token));
        return (ttl == null || ttl < 0) ? null : ttl.intValue();
    }
}
