package com.aviasalestickets.model.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayTicketResponse {
    private List<Long> ticketsId;
    private String message;
}
