package kr.hhplus.be.server.layered.catalog;

import kr.hhplus.be.server.layered.catalog.dto.SeatAvailabilityDto;

import java.time.LocalDate;
import java.util.List;

public interface CatalogRepository {
    List<LocalDate> findAvailableDates();
    List<SeatAvailabilityDto> findSeatsByDate(LocalDate date);
}
