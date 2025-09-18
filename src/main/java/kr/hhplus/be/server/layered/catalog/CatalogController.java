package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.dto.ShowAvailabilityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    /** 공연 회차별 가용성 요약 조회 */
    @GetMapping("/{concertId}/shows")
    public List<ShowAvailabilityDto> getShows(@PathVariable UUID concertId) {
        return catalogService.listShowsWithAvailability(concertId);
    }

    /** 특정 회차의 좌석 상세 가용성 조회 */
    @GetMapping("/shows/{scheduleId}/seats")
    public List<SeatAvailabilityDto> getSeats(
            @PathVariable UUID scheduleId,
            @RequestParam(required = false) UUID userId
    ) {
        return catalogService.listSeatAvailability(scheduleId, userId);
    }
}
