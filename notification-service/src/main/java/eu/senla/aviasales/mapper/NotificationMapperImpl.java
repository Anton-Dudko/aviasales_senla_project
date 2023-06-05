package eu.senla.aviasales.mapper;

import eu.senla.aviasales.model.entity.EmailNotification;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public EmailNotification recordToEntity(ConsumerRecord<String, Map<String, Object>> consumerRecord) {
        log.info("... method recordToEntity ");
        return EmailNotification.builder()
                .templateType(consumerRecord.topic())
                .templateVariables(consumerRecord.value())
                .dateFirstSend(LocalDate.now())
                .receiver((String) consumerRecord.value().get("email"))
                .build();
    }
}
