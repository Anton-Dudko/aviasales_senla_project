package eu.senla.aviasales.kafka;

import eu.senla.aviasales.mapper.NotificationMapper;
import eu.senla.aviasales.service.SendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EmailKafkaListener {

    private final SendService sendService;
    private final NotificationMapper notificationMapper;

    @KafkaListener(topics = "#{templatesConfig.getTopicNames()}", autoStartup = "true")
    public void listenTo(@NotNull ConsumerRecord<String, Map<String, Object>> consumerRecord) {
        log.info("KAFKA EMAIL NOTIFICATION LISTENER ACTIVATED");
        sendService.sendEmail(notificationMapper.recordToEntity(consumerRecord));
    }
}

