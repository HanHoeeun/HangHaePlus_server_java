package kr.hhplus.be.server.clean.wallet.domain.entity;

import java.util.UUID;

public class Wallet {
    private final UUID userId;
    private long balance;

    public Wallet(UUID userId, long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public UUID getUserId() { return userId; }
    public long getBalance() { return balance; }

    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance += amount;
    }
}
