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

    /** 잔액 충전 */
    public void charge(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.balance += amount;
    }

    /** 잔액 차감 */
    public void deduct(long amount) {
        if (balance < amount) {
            throw new IllegalStateException("잔액 부족");
        }
        this.balance -= amount;
    }

}
