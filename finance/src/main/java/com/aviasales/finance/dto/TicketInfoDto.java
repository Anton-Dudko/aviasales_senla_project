package com.aviasales.finance.dto;

import com.aviasales.finance.enums.TicketStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketInfoDto {
    private String id;
    private Long flightId;
    private BigDecimal price;
    private TicketStatus status;
    private Long userId;
}
