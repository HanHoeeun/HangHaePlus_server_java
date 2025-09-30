package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.clean.reservation.port.out.PaymentServicePort;
import kr.hhplus.be.server.clean.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.domain.enums.SeatStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmReservationServiceTest {

    @InjectMocks
    private ConfirmReservationService confirmReservationService;

    @Mock
    private ReservationRepositoryPort reservationRepository;

    @Mock
    private PaymentServicePort paymentService;

    @Test
    @DisplayName("결제 성공 시 → RESERVED 상태로 변경")
    void confirm_success() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = new Reservation(reservationId, UUID.randomUUID(), SeatStatus.HOLD, 15000);

        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(paymentService.pay(15000)).willReturn(true);
        given(reservationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        Reservation result = confirmReservationService.confirm(new ConfirmReservationCommand(reservationId, 15000));

        assertThat(result.getStatus()).isEqualTo(SeatStatus.RESERVED);
        verify(paymentService).pay(15000);
        verify(reservationRepository).save(reservation);
    }

    @Test
    @DisplayName("결제 실패 시 → 예외 발생")
    void confirm_fail_payment() {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = new Reservation(reservationId, UUID.randomUUID(), SeatStatus.HOLD, 15000);

        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(paymentService.pay(15000)).willReturn(false);

        assertThrows(IllegalStateException.class,
                () -> confirmReservationService.confirm(new ConfirmReservationCommand(reservationId, 15000)));
    }
}
