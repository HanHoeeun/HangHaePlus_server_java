package kr.hhplus.be.server.clean.reservation.application;
public interface ReserveSeatUseCase {
    ReserveSeatResult reserve(ReserveSeatCommand cmd);
}
