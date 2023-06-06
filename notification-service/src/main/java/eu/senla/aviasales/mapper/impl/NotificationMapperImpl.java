package eu.senla.aviasales.mapper.impl;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.mapper.NotificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

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
                .receiver((String) consumerRecord.value().get("email"))
                .build();
    }
}
