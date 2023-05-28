package com.aviasalestickets.service;

import com.aviasalestickets.model.dto.KafkaTicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String newTicketTopic = "new_ticket_reservation_event";
    private final String canceledTicketTopic = "canceled_ticket_reservation_event";

    public void sendNewTicket(KafkaTicketDto ticketDto) {
        kafkaTemplate.send(newTicketTopic, ticketDto);
    }

    public void sendCanceledTicket(KafkaTicketDto ticketDto) {
        kafkaTemplate.send(canceledTicketTopic, ticketDto);
    }



}
