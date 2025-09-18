package kr.hhplus.be.server.clean.wallet.port.out;

import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepositoryPort {
    Optional<Wallet> findByUserId(UUID userId);
    Wallet save(Wallet wallet);
}
