package kr.hhplus.be.server.clean.catalog.adapter.in.web;

import kr.hhplus.be.server.clean.catalog.application.service.CatalogQueryService;
import kr.hhplus.be.server.clean.catalog.application.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.clean.catalog.application.dto.ShowAvailabilityDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class CatalogController {

    private final CatalogQueryService service;

    public CatalogController(CatalogQueryService service) {
        this.service = service;
    }

    /** 예약 가능한 날짜(회차) 목록 */
    @GetMapping("/concerts/{concertId}/shows")
    public ResponseEntity<List<ShowAvailabilityDto>> listShows(
            @PathVariable UUID concertId) {
        return ResponseEntity.ok(service.listShowsWithAvailability(concertId));
    }

    /** 특정 회차의 좌석 가용성 (호출자 기준 SELF/OTHERS 라벨링) */
    @GetMapping("/concerts/{concertId}/shows/{scheduleId}/seats")
    public ResponseEntity<List<SeatAvailabilityDto>> listSeats(
            @PathVariable UUID concertId,
            @PathVariable UUID scheduleId,
            @AuthenticationPrincipal Jwt jwt // (임시) 없으면 null → SELF 라벨링 생략
    ) {
        UUID userId = null;
        if (jwt != null) {
            // sub 또는 user_id 등, 프로젝트 규약에 맞게 선택
            String claim = jwt.getClaimAsString("sub");
            if (claim == null) claim = jwt.getClaimAsString("user_id");
            if (claim != null) userId = UUID.fromString(claim);
        }
        return ResponseEntity.ok(service.listSeatAvailability(scheduleId, userId));
    }
}
