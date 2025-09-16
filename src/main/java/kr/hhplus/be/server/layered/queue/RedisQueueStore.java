package kr.hhplus.be.server.layered.queue;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static kr.hhplus.be.server.layered.queue.QueueKeys.*;

@Repository
public class RedisQueueStore implements QueueStore {
    private final StringRedisTemplate redis;

    public RedisQueueStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override public Optional<String> getUserToken(String userId) {
        return Optional.ofNullable(redis.opsForValue().get(userToken(userId)));
    }

    @Override public void setUserToken(String userId, String token) {
        redis.opsForValue().set(userToken(userId), token);
    }

    @Override public void addWaiting(String token, long scoreMillis) {
        redis.opsForZSet().add(waitingZset(), token, scoreMillis);
    }

    @Override public Long rankInWaiting(String token) {
        return redis.opsForZSet().rank(waitingZset(), token);
    }

    @Override public boolean removeFromWaiting(String token) {
        Long removed = redis.opsForZSet().remove(waitingZset(), token);
        return removed != null && removed > 0;
    }

    @Override public void putTokenHash(String token, Map<String, String> fields) {
        redis.opsForHash().putAll(tokenHash(token), fields);
    }

    @Override public boolean hasTokenHash(String token) {
        return Boolean.TRUE.equals(redis.hasKey(tokenHash(token)));
    }

    @Override public boolean isActive(String token) {
        return Boolean.TRUE.equals(redis.hasKey(activeKey(token)));
    }

    @Override public void setActive(String token, String userId, int ttlSeconds) {
        redis.opsForValue().set(activeKey(token), userId, Duration.ofSeconds(ttlSeconds));
    }

    @Override public Integer activeTtlSeconds(String token) {
        Long ttl = redis.getExpire(activeKey(token));
        return (ttl == null || ttl < 0) ? null : ttl.intValue();
    }
}
