package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.domain.enums.SeatStatus;
import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.dto.ShowAvailabilityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ConcertScheduleRepository scheduleRepo;
    private final SeatRepository seatRepo;
    private final ReservationRepository reservationRepo;
    private final Clock clock;

    /** 공연(회차) 목록 + 각 회차의 예약 가능 좌석 수 */
    public List<ShowAvailabilityDto> listShowsWithAvailability(UUID concertId) {
        var shows = scheduleRepo.findByConcertIdOrderByShowAtAsc(concertId);
        var now = Instant.now(clock);
        List<ShowAvailabilityDto> result = new ArrayList<>(shows.size());

        for (var show : shows) {
            var seats = seatRepo.findByScheduleIdOrderBySeatNumberAsc(show.getId());
            var holds = reservationRepo.findActiveHolds(show.getId(), now)
                    .stream()
                    .collect(Collectors.groupingBy(Reservation::getSeatNumber, Collectors.counting()));

            int total = seats.size();
            int available = 0;

            for (var seat : seats) {
                boolean sold = seat.getStatus() == SeatStatus.SOLD;
                boolean held = holds.containsKey(seat.getSeatNumber());
                if (!sold && !held) available++;
            }

            result.add(new ShowAvailabilityDto(
                    show.getId(),
                    show.getShowAt(),
                    total,
                    available
            ));
        }
        return result;
    }

    /** 특정 회차의 좌석 상세 가용성 */
    public List<SeatAvailabilityDto> listSeatAvailability(UUID scheduleId, UUID userId) {
        var now = Instant.now(clock);
        var seats = seatRepo.findByScheduleIdOrderBySeatNumberAsc(scheduleId);

        // seatNumber → (userId, holdExpireAt)
        record HoldInfo(UUID userId, Instant exp) {}
        Map<Integer, HoldInfo> holdMap = reservationRepo.findActiveHolds(scheduleId, now).stream()
                .collect(Collectors.toMap(
                        Reservation::getSeatNumber,
                        r -> new HoldInfo(r.getUserId(), r.getHoldExpiresAt()),
                        (a, b) -> a
                ));

        List<SeatAvailabilityDto> out = new ArrayList<>(seats.size());
        for (var seat : seats) {
            String label;
            Instant exp = null;

            if (seat.getStatus() == SeatStatus.SOLD) {
                label = "SOLD";
            } else {
                HoldInfo h = holdMap.get(seat.getSeatNumber());
                if (h == null) {
                    label = "AVAILABLE";
                } else {
                    exp = h.exp();
                    label = (userId != null && userId.equals(h.userId()))
                            ? "HELD_BY_SELF"
                            : "HELD_BY_OTHERS";
                }
            }
            out.add(new SeatAvailabilityDto(seat.getSeatNumber(), seat.getPrice(), label, exp));
        }
        return out;
    }
}
