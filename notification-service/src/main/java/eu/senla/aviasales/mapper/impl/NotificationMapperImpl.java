package eu.senla.aviasales.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.mapper.NotificationMapper;
import eu.senla.aviasales.request.CustomEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationMapperImpl implements NotificationMapper {

    private final ObjectMapper objectMapper;

    @Override
    public EmailNotification recordToEntity(@NotNull ConsumerRecord<String, Map<String, Object>> consumerRecord) {
        log.info("... method recordToEntity ");
        return EmailNotification.builder()
                .templateType(consumerRecord.topic())
                .templateVariables(consumerRecord.value())
                .receiver((String) consumerRecord.value().get("email"))
                .build();
    }

    @Override
    public EmailNotification customEmailRequestToEntity(@NotNull String topic,
                                                        @NotNull CustomEmailRequest customEmailRequest) {
        return EmailNotification.builder()
                .templateType(topic)
                .subject(customEmailRequest.getSubject())
                .receiver(customEmailRequest.getEmail())
                .templateVariables(objectMapper.convertValue(customEmailRequest, Map.class))
                .build();
    }
}
