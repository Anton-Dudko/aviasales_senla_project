package eu.senla.aviasales.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.request.CustomEmailRequest;
import eu.senla.aviasales.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SendToKafkaService {

    private final Producer<String, Map<String, CustomEmailRequest>> producer;
    private final ObjectMapper objectMapper;

    public SendResponse sendCustomEmail(String topic, final CustomEmailRequest customEmailRequest) {
        ProducerRecord<String, Map<String, CustomEmailRequest>> producerRecord =
                new ProducerRecord<>(topic, objectMapper.convertValue(customEmailRequest, Map.class));
        producer.send(producerRecord);
        log.info("...method sendCustomEmail {}", producerRecord);
        return SendResponse.builder()
                .email(customEmailRequest.getEmail())
                .subject(customEmailRequest.getSubject())
                .message("Notification sent. ")
                .build();
    }
}
