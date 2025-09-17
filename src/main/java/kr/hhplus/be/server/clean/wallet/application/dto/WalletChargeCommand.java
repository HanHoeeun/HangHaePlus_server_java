package kr.hhplus.be.server.clean.wallet.application.dto;

import java.util.UUID;

public record WalletChargeCommand(UUID userId, long amount) {}
