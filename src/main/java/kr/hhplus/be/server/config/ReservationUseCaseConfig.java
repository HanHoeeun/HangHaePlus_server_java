// config/ReservationUseCaseConfig.java
package kr.hhplus.be.server.config;

import kr.hhplus.be.server.clean.reservation.application.*;
import kr.hhplus.be.server.clean.reservation.port.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.*;

@Configuration
public class ReservationUseCaseConfig {

    @Bean
    ReserveSeatUseCase reserveSeatUseCase(SeatQueryPort seatQuery,
                                          ReservationQueryPort reservationQuery,
                                          ReservationCommandPort reservationCmd,
                                          SeatLockPort lock,
                                          TimeProvider timeProvider) {
        return new ReserveSeatService(seatQuery, reservationQuery, reservationCmd, lock,
                timeProvider, Duration.ofMinutes(5), Duration.ofSeconds(1));
    }

    @Bean TimeProvider timeProvider(java.time.Clock clock) { return clock::instant; }
}
