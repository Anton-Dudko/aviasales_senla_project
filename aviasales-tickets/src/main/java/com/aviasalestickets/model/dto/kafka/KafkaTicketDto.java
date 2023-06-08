package com.aviasalestickets.model.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
