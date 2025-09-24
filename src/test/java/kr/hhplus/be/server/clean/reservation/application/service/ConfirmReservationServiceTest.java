package kr.hhplus.be.server.clean.reservation.application.service;

import kr.hhplus.be.server.clean.reservation.application.dto.ConfirmReservationCommand;
import kr.hhplus.be.server.clean.reservation.domain.entity.Reservation;
import kr.hhplus.be.server.clean.reservation.port.out.ReservationRepositoryPort;
import kr.hhplus.be.server.clean.wallet.port.out.WalletServicePort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)   // ✅ AutoCloseable 경고 제거
class ConfirmReservationServiceTest {

    @InjectMocks
    private ConfirmReservationService confirmReservationService;

    @Mock
    private ReservationRepositoryPort reservationRepository;

    @Mock
    private WalletServicePort walletServicePort;

    private final UUID reservationId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private final Reservation reservation =
            new Reservation(reservationId, userId, UUID.randomUUID(), SeatStatus.HOLD, 10000L);

    @Test
    @DisplayName("결제 성공 시 → 예약 확정 (RESERVED)")
    void confirmReservation_success() {
        // given
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(walletServicePort.deduct(userId, 10000L)).willReturn(true);
        given(reservationRepository.save(any(Reservation.class))).willAnswer(inv -> inv.getArgument(0));

        // when
        Reservation confirmed = confirmReservationService.confirm(
                new ConfirmReservationCommand(reservationId, 10000L));

        // then
        assertThat(confirmed.getStatus()).isEqualTo(SeatStatus.RESERVED);
        verify(walletServicePort).deduct(userId, 10000L);
        verify(reservationRepository).save(confirmed);
    }

    @Test
    @DisplayName("결제 실패 시 → IllegalStateException 발생")
    void confirmReservation_fail_dueToPayment() {
        // given
        given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));
        given(walletServicePort.deduct(userId, 10000L)).willReturn(false);

        // when & then
        assertThrows(IllegalStateException.class, () ->
                confirmReservationService.confirm(
                        new ConfirmReservationCommand(reservationId, 10000L))
        );
    }
}
