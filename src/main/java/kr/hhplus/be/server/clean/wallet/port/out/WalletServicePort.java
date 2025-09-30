package kr.hhplus.be.server.clean.wallet.port.out;

import java.util.UUID;

public interface WalletServicePort {
    boolean deduct(UUID userId, long amount);  // pay → deduct 로 명확히
}
