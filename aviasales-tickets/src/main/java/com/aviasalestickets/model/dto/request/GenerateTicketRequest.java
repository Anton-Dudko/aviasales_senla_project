package com.aviasalestickets.model.dto.request;

import lombok.Value;

import javax.validation.constraints.Size;

@Value
public class GenerateTicketRequest {
    Long flightId;
    @Size(max = 100)
    Integer countTicketsFirstClass;
    @Size(max = 100)
    Integer countTicketsSecondClass;
    Double firstClassPrice;
    Double secondClassPrice;
}
