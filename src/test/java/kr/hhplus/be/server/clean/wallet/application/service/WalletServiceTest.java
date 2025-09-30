package kr.hhplus.be.server.clean.wallet.application.service;

import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;
import kr.hhplus.be.server.clean.wallet.port.out.WalletRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepositoryPort walletRepository;

    @Test
    @DisplayName("지갑 충전 성공 → 기존 지갑이 없으면 새로 생성")
    void charge_newWallet() {
        // given
        UUID userId = UUID.randomUUID();
        WalletChargeCommand command = new WalletChargeCommand(userId, 5000L);

        given(walletRepository.findByUserId(userId)).willReturn(Optional.empty());
        given(walletRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        WalletBalanceResult result = walletService.charge(command);

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(5000L);
        verify(walletRepository).save(any(Wallet.class));
    }

    @Test
    @DisplayName("지갑 충전 성공 → 기존 지갑이 있으면 잔액 누적")
    void charge_existingWallet() {
        // given
        UUID userId = UUID.randomUUID();
        Wallet existing = new Wallet(userId, 3000L);

        given(walletRepository.findByUserId(userId)).willReturn(Optional.of(existing));
        given(walletRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        // when
        WalletBalanceResult result = walletService.charge(new WalletChargeCommand(userId, 2000L));

        // then
        assertThat(result.balance()).isEqualTo(5000L);
        verify(walletRepository).save(existing);
    }

    @Test
    @DisplayName("잔액 조회 성공 → 지갑이 없으면 0원으로 반환")
    void getBalance_newWallet() {
        // given
        UUID userId = UUID.randomUUID();
        given(walletRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when
        WalletBalanceResult result = walletService.getBalance(userId.toString());

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(0L);
    }

    @Test
    @DisplayName("잔액 조회 성공 → 기존 지갑이 있으면 잔액 반환")
    void getBalance_existingWallet() {
        // given
        UUID userId = UUID.randomUUID();
        Wallet existing = new Wallet(userId, 7000L);
        given(walletRepository.findByUserId(userId)).willReturn(Optional.of(existing));

        // when
        WalletBalanceResult result = walletService.getBalance(userId.toString());

        // then
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.balance()).isEqualTo(7000L);
    }
}
