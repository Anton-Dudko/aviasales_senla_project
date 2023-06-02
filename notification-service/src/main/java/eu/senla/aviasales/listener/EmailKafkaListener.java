package eu.senla.aviasales.listener;

import eu.senla.aviasales.service.MessageBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailKafkaListener {
    private final MessageBuilderService messageBuilderService;

    @KafkaListener(topics = "#{templatesConfig.getTopicNames()}", autoStartup = "true")
    public void listenTo(ConsumerRecord<String, Map<String, Object>> record) {
        log.info("KAFKA EMAIL NOTIFICATION LISTENER ACTIVATED");
        messageBuilderService.buildAndSend(record.topic(), record.value());
    }
}

