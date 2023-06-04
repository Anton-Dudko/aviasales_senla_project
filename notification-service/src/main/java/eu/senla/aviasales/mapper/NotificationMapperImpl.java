package eu.senla.aviasales.mapper;

import eu.senla.aviasales.model.entity.EmailNotification;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public EmailNotification recordToEntity(ConsumerRecord<String, Map<String, Object>> consumerRecord) {
        return EmailNotification.builder()
                .templateType(consumerRecord.topic())
                .templateVariables(consumerRecord.value())
                .dateFirstSend(LocalDate.now())
                .receiver((String) consumerRecord.value().get("email"))
                .build();
    }
}
