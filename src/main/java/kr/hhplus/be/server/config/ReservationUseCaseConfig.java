package kr.hhplus.be.server.config;

import kr.hhplus.be.server.clean.reservation.application.service.ReserveSeatService;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatLockPort;
import kr.hhplus.be.server.clean.reservation.port.out.SeatRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReservationUseCaseConfig {

    @Bean
    ReserveSeatUseCase reserveSeatUseCase(SeatRepositoryPort seatRepository,
                                          SeatLockPort seatLockPort) {
        return new ReserveSeatService(seatRepository, seatLockPort);
    }

}
