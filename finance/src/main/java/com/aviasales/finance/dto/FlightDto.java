package com.aviasales.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightDto {
    private long id;
    private LocalDateTime departureDateTime;
}
