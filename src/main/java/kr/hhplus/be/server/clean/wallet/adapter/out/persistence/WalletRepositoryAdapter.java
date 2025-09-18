package kr.hhplus.be.server.clean.wallet.adapter.out.persistence;

import kr.hhplus.be.server.clean.wallet.domain.entity.Wallet;
import kr.hhplus.be.server.clean.wallet.port.out.WalletRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WalletRepositoryAdapter implements WalletRepositoryPort {

    private final Map<UUID, Wallet> store = new HashMap<>();

    @Override
    public Optional<Wallet> findByUserId(UUID userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public Wallet save(Wallet wallet) {
        store.put(wallet.getUserId(), wallet);
        return wallet;
    }
}
