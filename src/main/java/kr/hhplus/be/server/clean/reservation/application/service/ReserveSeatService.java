package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.clean.reservation.port.out.SeatLockPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ReserveSeatService implements ReserveSeatUseCase {

    private final SeatRepositoryPort seatRepository;
    private final SeatLockPort seatLockPort;

    public ReserveSeatService(SeatRepositoryPort seatRepository, SeatLockPort seatLockPort) {
        this.seatRepository = seatRepository;
        this.seatLockPort = seatLockPort;
    }

    @Override
    public ReserveSeatResult reserve(ReserveSeatCommand command) {
        UUID seatId = command.seatId();
        seatLockPort.lock(seatId);

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));

        seat.hold();
        seatRepository.save(seat);

        // 임시배정 만료 시간 (5분 뒤)
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        return new ReserveSeatResult(
                UUID.randomUUID(),           // reservationId (실제로는 Reservation 엔티티 ID)
                seat.getStatus().name(),     // HOLD
                expiresAt,
                seat.getPrice()              // totalAmount
        );
    }
}
