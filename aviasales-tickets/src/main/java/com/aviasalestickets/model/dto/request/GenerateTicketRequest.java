package com.aviasalestickets.model.dto.request;

import lombok.Value;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Value
@Validated
public class GenerateTicketRequest {
    Long flightId;
    @Min(value = 0)
    @Max(value = 100)
    Integer countTicketsFirstClass;
    @Min(0)
    @Max(100)
    Integer countTicketsSecondClass;
    Double firstClassPrice;
    Double secondClassPrice;
}
