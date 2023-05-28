package com.aviasalestickets.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class KafkaTicketDto {
    private String userLanguage;
    private String email;
    private String userName;
    private String from;
    private String to;
    private LocalDate ticketDate;
    private BigDecimal price;

}
