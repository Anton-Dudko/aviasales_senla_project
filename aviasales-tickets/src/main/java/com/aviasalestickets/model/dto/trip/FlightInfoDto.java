package com.aviasalestickets.model.dto.trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightInfoDto {
    private Trip trip;
    private LocalDateTime departureDateTime;
}
