package kr.hhplus.be.server.clean.reservation.adapter.in.web;

import kr.hhplus.be.server.clean.reservation.application.dto.AvailableDatesResult;
import kr.hhplus.be.server.clean.reservation.application.dto.AvailableSeatsResult;
import kr.hhplus.be.server.clean.reservation.port.in.ReservationQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationQueryController {

    private final ReservationQueryUseCase reservationQueryUseCase;

    // 예약 가능 날짜 조회
    @GetMapping("/dates")
    public List<AvailableDatesResult> getAvailableDates() {
        return reservationQueryUseCase.getAvailableDates();
    }

    // 특정 날짜(스케줄)의 좌석 조회
    @GetMapping("/dates/{scheduleId}/seats")
    public List<AvailableSeatsResult> getAvailableSeats(@PathVariable UUID scheduleId) {
        return reservationQueryUseCase.getAvailableSeats(scheduleId);
    }
}
