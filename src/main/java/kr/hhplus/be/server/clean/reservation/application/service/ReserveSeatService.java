package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ReserveSeatResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Seat;
import kr.hhplus.be.server.domain.enums.ReservationStatus;
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

        // 1️⃣ 좌석 상태 변경 (HOLD 등)
        seat.hold();
        seatRepository.save(seat);

        // 2️⃣ 임시배정 만료 시간 (5분 뒤)
        Instant expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES);

        // 3️⃣ 예약 ID (실제 구현에서는 Reservation 생성 후 ID 반환)
        UUID reservationId = UUID.randomUUID();

        // 4️⃣ 결과 반환 (신규 필드 포함)
        return new ReserveSeatResult(
                reservationId,
                seat.getId(),
                seat.getSeatNumber(),
                ReservationStatus.HELD,
                expiresAt,
                seat.getPrice()
        );
    }
}
