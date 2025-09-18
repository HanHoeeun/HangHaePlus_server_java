package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;
import kr.hhplus.be.server.layered.catalog.dto.ShowAvailabilityDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CatalogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CatalogController controller = new CatalogController(catalogService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("공연 회차별 가용성 조회 API")
    void getShows() throws Exception {
        // given
        UUID concertId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        var dto = new ShowAvailabilityDto(scheduleId, Instant.now(), 50, 40);

        given(catalogService.listShowsWithAvailability(concertId))
                .willReturn(List.of(dto));

        // when & then
        mockMvc.perform(get("/api/v1/catalog/{concertId}/shows", concertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalSeats").value(50))
                .andExpect(jsonPath("$[0].availableSeats").value(40));
    }

    @Test
    @DisplayName("특정 회차의 좌석 상세 조회 API")
    void getSeats() throws Exception {
        // given
        UUID scheduleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        var seat1 = new SeatAvailabilityDto(1, 10000, "AVAILABLE", null);
        var seat2 = new SeatAvailabilityDto(2, 10000, "SOLD", null);

        given(catalogService.listSeatAvailability(scheduleId, userId))
                .willReturn(List.of(seat1, seat2));

        // when & then
        mockMvc.perform(get("/api/v1/catalog/shows/{scheduleId}/seats", scheduleId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value(1))
                .andExpect(jsonPath("$[0].label").value("AVAILABLE"))
                .andExpect(jsonPath("$[1].seatNumber").value(2))
                .andExpect(jsonPath("$[1].label").value("SOLD"));
    }
}
