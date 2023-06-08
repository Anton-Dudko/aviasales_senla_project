package com.aviasalestickets.service;

import com.aviasalestickets.model.dto.kafka.KafkaTicketDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final ObjectMapper objectMapper;
    private final Producer<String, Map<String, KafkaTicketDto>> producer;

    public KafkaTicketDto sendMessage(KafkaTicketDto ticketDto, String topic){
        ProducerRecord<String, Map<String, KafkaTicketDto>> producerRecord =
                new ProducerRecord<>(topic, objectMapper.convertValue(ticketDto, Map.class));
        log.info("producerRecord ... {}", producerRecord);
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);
        return ticketDto;
    }

}
