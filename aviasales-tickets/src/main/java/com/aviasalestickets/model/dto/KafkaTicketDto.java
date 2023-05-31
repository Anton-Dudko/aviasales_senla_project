package com.aviasalestickets.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaTicketDto {
    private String userLanguage;
    private String email;
    private String userName;
    private String from;
    private String to;
    private String price;
    private String ticketDate;


}
