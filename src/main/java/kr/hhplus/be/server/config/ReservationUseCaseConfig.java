// config/ReservationUseCaseConfig.java
package kr.hhplus.be.server.config;

import kr.hhplus.be.server.clean.reservation.application.service.ReserveSeatService;
import kr.hhplus.be.server.clean.reservation.port.*;
import kr.hhplus.be.server.clean.reservation.port.in.ReserveSeatUseCase;
import kr.hhplus.be.server.clean.reservation.port.out.SeatLockPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;

@Configuration
public class ReservationUseCaseConfig {

    @Bean
    ReserveSeatUseCase reserveSeatUseCase(SeatRepositoryPort seatRepository,
                                          SeatLockPort seatLockPort) {
        return new ReserveSeatService(seatRepository, seatLockPort);
    }
}
