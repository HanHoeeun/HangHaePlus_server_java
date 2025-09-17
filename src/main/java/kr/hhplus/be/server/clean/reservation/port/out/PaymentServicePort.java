package kr.hhplus.be.server.clean.reservation.port.out;

public interface PaymentServicePort {
    boolean pay(long amount);
}
