package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import kr.hhplus.be.server.clean.reservation.port.out.SeatLockPort;

import java.util.UUID;

public class ReserveSeatService {
    private final SeatRepositoryPort seatRepository;
    private final SeatLockPort seatLockPort;

    public ReserveSeatService(SeatRepositoryPort seatRepository, SeatLockPort seatLockPort) {
        this.seatRepository = seatRepository;
        this.seatLockPort = seatLockPort;
    }

    public ReserveSeatResult reserve(ReserveSeatCommand command) {
        UUID seatId = command.seatId();
        seatLockPort.lock(seatId);

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석 없음"));

        seat.hold();
        seatRepository.save(seat);

        return new ReserveSeatResult(seat.getId(), seat.getStatus().name());
    }
}
