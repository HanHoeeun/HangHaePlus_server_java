package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.PaymentServicePort;
import kr.hhplus.be.server.clean.reservation.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmReservationService implements ConfirmReservationUseCase {

    private final ReservationRepositoryPort reservationRepository;
    private final PaymentServicePort paymentService;

    @Override
    public Reservation confirm(ConfirmReservationCommand command) {
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 없음"));

        if (!paymentService.pay(command.amount())) {
            throw new IllegalStateException("결제 실패");
        }

        reservation.confirmPayment();   // ✅ domain/entity/Reservation 메서드 활용
        return reservationRepository.save(reservation);
    }
}
