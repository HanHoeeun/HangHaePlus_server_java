package kr.hhplus.be.server.clean.wallet.application.dto;

import java.util.UUID;

public record WalletBalanceResult(UUID userId, long balance) {}
