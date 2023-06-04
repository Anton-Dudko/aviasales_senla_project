package eu.senla.aviasales.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.model.constant.KafkaTopicConstants;
import eu.senla.aviasales.model.dto.CustomEmailDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {

    private final Producer<String, Map<String, CustomEmailDto>> producer;
    private final ObjectMapper objectMapper;


    public void send(CustomEmailDto event) {
        ProducerRecord<String, Map<String, CustomEmailDto>> producerRecord =
                new ProducerRecord<>(KafkaTopicConstants.CUSTOM_EMAIL_TYPE, objectMapper.convertValue(event, Map.class));
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);
    }
}
