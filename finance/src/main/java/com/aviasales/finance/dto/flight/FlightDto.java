package com.aviasales.finance.dto.flight;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightDto {
    private long flightId;
    private LocalDateTime departureDateTime;
}
