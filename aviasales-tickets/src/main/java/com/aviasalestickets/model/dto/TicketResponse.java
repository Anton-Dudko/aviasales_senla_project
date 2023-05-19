package com.aviasalestickets.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponse {
    private Long id;

    private Long userId;

    private Long tripId;

    private String fio;

    private String type;

    private BigDecimal price;

    private String status;
}
