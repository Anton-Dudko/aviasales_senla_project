package com.aviasalestickets.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    private Long id;
    private Long userId;
    @NotNull
    private Long flightId;
    private String fio;
    @NotNull
    private String type;
    @NotNull
    private BigDecimal price;
    @NotNull
    private String status;
}
