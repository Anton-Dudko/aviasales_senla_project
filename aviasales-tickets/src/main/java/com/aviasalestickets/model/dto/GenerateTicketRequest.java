package com.aviasalestickets.model.dto;

import lombok.Value;

@Value
public class GenerateTicketRequest {
    Long flightId;
    Integer countTicketsFirstClass;
    Integer countTicketsSecondClass;
    Double firstClassPrice;
    Double secondClassPrice;
}
