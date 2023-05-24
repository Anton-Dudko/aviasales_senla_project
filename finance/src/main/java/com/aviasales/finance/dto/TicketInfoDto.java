package com.aviasales.finance.dto;

import com.aviasales.finance.enums.TicketStatus;
import lombok.Data;

@Data
public class TicketInfoDto {
    private String ticketId;
    private double price;
    private TicketStatus ticketStatus;
}
