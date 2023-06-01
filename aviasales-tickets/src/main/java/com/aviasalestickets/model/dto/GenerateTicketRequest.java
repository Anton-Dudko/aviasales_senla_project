package com.aviasalestickets.model.dto;

import lombok.Value;

@Value
public class GenerateTicketRequest {
    Long tripId;
    Integer countTicketsFirstClass;
    Integer countTicketsSecondClass;
    Double firstClassPrice;
    Double secondClassPrice;
}
