package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationResult;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.domain.enums.ReservationStatus;
import kr.hhplus.be.server.clean.reservation.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.PaymentServicePort;
import kr.hhplus.be.server.clean.reservation.port.out.ReservationRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfirmReservationService implements ConfirmReservationUseCase {

    private final ReservationRepositoryPort reservationRepository;
    private final PaymentServicePort paymentService;

    @Override
    public ConfirmReservationResult confirm(ConfirmReservationCommand command) {
        // 1️⃣ 예약 조회
        Reservation reservation = reservationRepository.findById(command.reservationId())
                .orElseThrow(() -> new IllegalArgumentException("예약 없음"));

        // 2️⃣ 결제 처리
        boolean paymentSuccess = paymentService.pay(command.amount());
        if (!paymentSuccess) {
            throw new IllegalStateException("결제 실패");
        }

        // 3️⃣ 예약 확정 처리 (도메인 상태 변경)
        reservation.confirmPayment();

        // 4️⃣ 저장 (확정된 예약 상태를 DB에 반영)
        reservationRepository.save(reservation);

        // 5️⃣ 결과 DTO로 변환 후 반환
        return new ConfirmReservationResult(
                reservation.getId(),
                reservation.getStatus() != null
                        ? reservation.getStatus()
                        : ReservationStatus.CONFIRMED // 널 방어 (도메인 상태 변경 미반영 대비)
        );
    }
}
