package kr.hhplus.be.server.clean.wallet.adapter.out.persistence;

import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;
import kr.hhplus.be.server.clean.wallet.port.out.WalletRepositoryPort;
import kr.hhplus.be.server.clean.wallet.port.out.WalletServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WalletServiceAdapter implements WalletServicePort {

    private final WalletRepositoryPort walletRepository;

    public WalletServiceAdapter(WalletRepositoryPort walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public boolean deduct(UUID userId, long amount) {
        return walletRepository.findByUserId(userId)
                .map(wallet -> {
                    if (wallet.getBalance() < amount) return false;
                    wallet.deduct(amount);         // ✅ Wallet 엔티티 메서드 호출
                    walletRepository.save(wallet);
                    return true;
                })
                .orElse(false);
    }
}

