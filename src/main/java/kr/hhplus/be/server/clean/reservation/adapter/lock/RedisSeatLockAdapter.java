package kr.hhplus.be.server.clean.reservation.adapter.lock;

import kr.hhplus.be.server.clean.reservation.port.SeatLockPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class RedisSeatLockAdapter implements SeatLockPort {
    private final StringRedisTemplate redis;
    public RedisSeatLockAdapter(StringRedisTemplate redis) { this.redis = redis; }

    @Override public boolean tryLock(String key, long millis) {
        String token = UUID.randomUUID().toString();
        Boolean ok = redis.opsForValue().setIfAbsent(key, token, Duration.ofMillis(millis));
        return Boolean.TRUE.equals(ok);
    }
    @Override public void unlock(String key) {

        redis.delete(key);
    }
}
