package kr.hhplus.be.server.clean.queue.application.service;

import kr.hhplus.be.server.clean.queue.application.dto.QueueIssueResult;
import kr.hhplus.be.server.clean.queue.application.dto.QueueStatusResult;
import kr.hhplus.be.server.clean.queue.domain.entity.QueueToken;
import kr.hhplus.be.server.clean.queue.port.out.QueueStorePort;
import kr.hhplus.be.server.domain.enums.QueueStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @InjectMocks
    private QueueService queueService;

    @Mock
    private QueueStorePort storePort;

    @Test
    @DisplayName("토큰 발급 시 → WAITING 상태 + position 반영")
    void issueToken_success() {
        // given
        UUID userId = UUID.randomUUID();
        given(storePort.countWaiting()).willReturn(5); // 현재 5명이 대기중

        // when
        QueueIssueResult result = queueService.issueToken(userId);

        // then
        assertThat(result.token()).startsWith("qtk_");
        assertThat(result.position()).isEqualTo(6);
        assertThat(result.status()).isEqualTo("WAITING");

        verify(storePort).save(any(QueueToken.class));
    }

    @Test
    @DisplayName("토큰 상태 조회 성공 → 저장된 토큰 정보 반환")
    void checkStatus_success() {
        // given
        UUID userId = UUID.randomUUID();
        QueueToken token = new QueueToken(userId, 3, QueueStatus.WAITING);

        given(storePort.findByToken(token.getToken())).willReturn(Optional.of(token));

        // when
        QueueStatusResult result = queueService.checkStatus(token.getToken());

        // then
        assertThat(result.position()).isEqualTo(3);
        assertThat(result.status()).isEqualTo("WAITING");
    }

    @Test
    @DisplayName("존재하지 않는 토큰 조회 시 → 예외 발생")
    void checkStatus_fail_notFound() {
        // given
        String invalidToken = "qtk_invalid";
        given(storePort.findByToken(invalidToken)).willReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> queueService.checkStatus(invalidToken));
    }
}
