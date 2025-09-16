package kr.hhplus.be.server.layered.queue;

import kr.hhplus.be.server.layered.queue.dto.QueueIssueResponse;
import kr.hhplus.be.server.layered.queue.dto.QueueStatusResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QueueServiceTest {

    QueueStore store = mock(QueueStore.class);
    Clock clock = Clock.fixed(Instant.parse("2025-09-03T12:00:00Z"), ZoneOffset.UTC);

    // activeTtlSeconds=600, promotePerSec=100 (ETA 계산용)
    QueueService sut = new QueueService(store, clock, 600, 100);

    /** 이미 ACTIVE 토큰이 있으면 기존 토큰을 그대로 반환한다(재발급/대기열 등록 안 함). */
    @Test
    void issueReturnsActiveIfAlreadyActive() {
        when(store.getUserToken("u1")).thenReturn(Optional.of("t1"));
        when(store.isActive("t1")).thenReturn(true);

        QueueIssueResponse res = sut.issue("u1");

        assertEquals("ACTIVE", res.status());
        assertEquals("t1", res.token());

        verify(store, never()).addWaiting(anyString(), anyLong());
        verify(store, never()).putTokenHash(anyString(), anyMap());
        verify(store, never()).setUserToken(anyString(), anyString());
    }

    /** 신규 유저는 WAITING 토큰을 발급받고 대기열에 들어간다. */
    @Test
    void issueNewTokenIntoWaiting() {
        when(store.getUserToken("u2")).thenReturn(Optional.empty());
        // 발급 직후 자신의 rank = 0 (position = 1)
        when(store.rankInWaiting(anyString())).thenReturn(0L);

        QueueIssueResponse res = sut.issue("u2");

        assertEquals("WAITING", res.status());
        assertEquals(1, res.position());
        // 등록 시각이 score로 사용
        verify(store).addWaiting(anyString(), eq(Instant.parse("2025-09-03T12:00:00Z").toEpochMilli()));
        verify(store).setUserToken(eq("u2"), anyString());
        verify(store).putTokenHash(anyString(), argThat((Map<String, String> m) -> "WAITING".equals(m.get("status"))));
    }

    /** 기존 WAITING 토큰이 있으면 순번을 유지하고, 새 토큰을 만들지 않는다. */
    @Test
    void issueReturnsExistingWaitingWithoutReissue() {
        when(store.getUserToken("u3")).thenReturn(Optional.of("t3"));
        when(store.isActive("t3")).thenReturn(false);
        when(store.rankInWaiting("t3")).thenReturn(5L); // 6번째

        QueueIssueResponse res = sut.issue("u3");

        assertEquals("WAITING", res.status());
        assertEquals("t3", res.token());
        assertEquals(6, res.position());
        // 재등록/재발급이 없어야 한다.
        verify(store, never()).addWaiting(anyString(), anyLong());
        verify(store, never()).putTokenHash(anyString(), anyMap());
        verify(store, never()).setUserToken(anyString(), anyString());
    }

    /** 상태 조회: ACTIVE면 남은 TTL을 반환한다. */
    @Test
    void statusActiveReturnsTtl() {
        when(store.isActive("t1")).thenReturn(true);
        when(store.activeTtlSeconds("t1")).thenReturn(540);

        QueueStatusResponse res = sut.status("t1");

        assertEquals("ACTIVE", res.status());
        assertEquals(540, res.activeTtlSeconds());
        assertNull(res.position());
        assertNull(res.etaSeconds());
    }

    /** 상태 조회: WAITING이면 순번과 ETA를 계산해 반환한다. */
    @Test
    void statusWaitingReturnsRankAndEta() {
        when(store.isActive("t2")).thenReturn(false);
        when(store.hasTokenHash("t2")).thenReturn(true);
        when(store.rankInWaiting("t2")).thenReturn(9L); // 10번째

        QueueStatusResponse res = sut.status("t2");

        assertEquals("WAITING", res.status());
        assertEquals(10, res.position());
        assertNotNull(res.etaSeconds());
    }

    /** 상태 조회: 해시는 남았지만 ZSET rank가 없으면 만료(EXPIRED)로 간주한다(불일치 자가치유). */
    @Test
    void statusExpiredWhenHashExistsButNotInWaiting() {
        when(store.isActive("t4")).thenReturn(false);
        when(store.hasTokenHash("t4")).thenReturn(true);
        when(store.rankInWaiting("t4")).thenReturn(null); // ZSET에는 없음

        QueueStatusResponse res = sut.status("t4");

        assertEquals("EXPIRED", res.status());
        assertNull(res.position());
        assertNull(res.etaSeconds());
        assertNull(res.activeTtlSeconds());
    }

    /** 상태 조회 ETA 산식 경계값: rank=0 → ETA=0초 */
    @Test
    void statusWaitingEtaRank0Zero() {
        when(store.isActive("t5")).thenReturn(false);
        when(store.hasTokenHash("t5")).thenReturn(true);
        when(store.rankInWaiting("t5")).thenReturn(0L);

        QueueStatusResponse res = sut.status("t5");

        assertEquals("WAITING", res.status());
        assertEquals(1, res.position());
        assertEquals(0, res.etaSeconds());
    }

    /** 상태 조회 ETA 산식 경계값: rank=99, promotePerSec=100 → ETA=0초 */
    @Test
    void statusWaitingEtaRank99Zero() {
        when(store.isActive("t6")).thenReturn(false);
        when(store.hasTokenHash("t6")).thenReturn(true);
        when(store.rankInWaiting("t6")).thenReturn(99L);

        QueueStatusResponse res = sut.status("t6");

        assertEquals("WAITING", res.status());
        assertEquals(100, res.position());
        assertEquals(0, res.etaSeconds()); // 99 / 100 = 0
    }

    /** 상태 조회 ETA 산식 경계값: rank=100, promotePerSec=100 → ETA=1초 */
    @Test
    void statusWaitingEtaRank100One() {
        when(store.isActive("t7")).thenReturn(false);
        when(store.hasTokenHash("t7")).thenReturn(true);
        when(store.rankInWaiting("t7")).thenReturn(100L);

        QueueStatusResponse res = sut.status("t7");

        assertEquals("WAITING", res.status());
        assertEquals(101, res.position());
        assertEquals(1, res.etaSeconds()); // 100 / 100 = 1
    }

    /** 발급: 기존 토큰 포인터는 있는데 ACTIVE/WAITING 모두에 없다면 새 토큰을 발급한다(자가치유). */
    @Test
    void issueInconsistentPointerTriggersNewIssue() {
        when(store.getUserToken("u5")).thenReturn(Optional.of("t5")); // 포인터만 존재
        when(store.isActive("t5")).thenReturn(false);
        when(store.rankInWaiting("t5")).thenReturn(null); // 어디에도 없음

        // 추가 후 새 토큰의 순위
        when(store.rankInWaiting(anyString())).thenReturn(0L);

        QueueIssueResponse res = sut.issue("u5");

        assertEquals("WAITING", res.status());
        assertEquals(1, res.position());

        // 새 토큰이 setUserToken으로 저장되어야 하고, 이전 토큰("t5")와 달라야 한다.
        ArgumentCaptor<String> newTokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(store).setUserToken(eq("u5"), newTokenCaptor.capture());
        assertNotEquals("t5", newTokenCaptor.getValue());

        verify(store).addWaiting(eq(newTokenCaptor.getValue()),
                eq(Instant.parse("2025-09-03T12:00:00Z").toEpochMilli()));
        verify(store).putTokenHash(eq(newTokenCaptor.getValue()), anyMap());
    }

    /** 상태 조회: 토큰 자체가 완전히 없으면 EXPIRED */
    @Test
    void statusExpiredWhenUnknownToken() {
        when(store.isActive("gone")).thenReturn(false);
        when(store.hasTokenHash("gone")).thenReturn(false);

        QueueStatusResponse res = sut.status("gone");

        assertEquals("EXPIRED", res.status());
    }
}
