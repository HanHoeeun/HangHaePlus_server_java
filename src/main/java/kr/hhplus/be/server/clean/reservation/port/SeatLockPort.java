package kr.hhplus.be.server.clean.reservation.port;
public interface SeatLockPort {
    boolean tryLock(String key, long millis);
    void unlock(String key);
    static String key(Object showId, int seatNo) { return "lock:seat:%s:%d".formatted(showId, seatNo); }
}
