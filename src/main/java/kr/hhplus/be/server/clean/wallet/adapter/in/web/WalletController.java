package kr.hhplus.be.server.clean.wallet.adapter.in.web;

import kr.hhplus.be.server.clean.wallet.application.dto.WalletBalanceResult;
import kr.hhplus.be.server.clean.wallet.application.dto.WalletChargeCommand;
import kr.hhplus.be.server.clean.wallet.port.in.WalletUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletUseCase walletUseCase;

    @PostMapping("/charge")
    public WalletBalanceResult charge(@RequestParam UUID userId, @RequestParam long amount) {
        return walletUseCase.charge(new WalletChargeCommand(userId, amount));
    }

    @GetMapping("/balance/{userId}")
    public WalletBalanceResult getBalance(@PathVariable String userId) {
        return walletUseCase.getBalance(userId);
    }
}
