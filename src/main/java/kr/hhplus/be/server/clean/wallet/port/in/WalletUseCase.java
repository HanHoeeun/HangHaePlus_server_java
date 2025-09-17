package kr.hhplus.be.server.clean.wallet.port.in;

import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;

public interface WalletUseCase {
    WalletBalanceResult charge(WalletChargeCommand command);
    WalletBalanceResult getBalance(String userId);
}
