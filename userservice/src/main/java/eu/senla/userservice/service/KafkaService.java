package eu.senla.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.mapper.UserMapper;
import eu.senla.userservice.response.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaService {

    private final Producer<String, Map<String, UserEvent>> producer;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;


    public void sendToTopic(@NotNull String topic,
                            @NotNull User user) {
        UserEvent userEvent = createEvent(user);
        send(topic, userEvent);
    }

    public void sendToTopic(@NotNull String topic,
                            @NotNull User user,
                            @NotNull String password) {
        UserEvent userEvent = createEvent(user);
        userEvent.setNewPassword(password);
        send(topic, userEvent);
    }

    private void send(@NotNull String topic,
                      @NotNull UserEvent userEvent) {
        ProducerRecord<String, Map<String, UserEvent>> producerRecord =
                new ProducerRecord<>(topic, objectMapper.convertValue(userEvent, Map.class));
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);
    }

    private UserEvent createEvent(@NotNull User user) {
        UserEvent event = userMapper.entityToEvent(user);
        log.info("event ... {}", event);
        return event;
    }
}
