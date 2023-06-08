package com.aviasales.finance.dto.ticket;

import com.aviasales.finance.enums.TicketStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketInfoDto {
    private Long id;
    private Long flightId;
    private BigDecimal price;
    private TicketStatus status;
    private Long userId;
}
