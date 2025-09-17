package kr.hhplus.be.server.clean.wallet.application.service;

import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;
import kr.hhplus.be.server.clean.wallet.port.in.WalletUseCase;
import kr.hhplus.be.server.clean.wallet.port.out.WalletRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class WalletService implements WalletUseCase {

    private final WalletRepositoryPort walletRepository;

    @Override
    public WalletBalanceResult charge(WalletChargeCommand command) {
        Wallet wallet = walletRepository.findByUserId(command.userId())
                .orElse(new Wallet(command.userId(), 0));
        wallet.charge(command.amount());
        Wallet saved = walletRepository.save(wallet);
        return new WalletBalanceResult(saved.getUserId(), saved.getBalance());
    }

    @Override
    public WalletBalanceResult getBalance(String userIdStr) {
        UUID userId = UUID.fromString(userIdStr);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElse(new Wallet(userId, 0));
        return new WalletBalanceResult(wallet.getUserId(), wallet.getBalance());
    }
}
