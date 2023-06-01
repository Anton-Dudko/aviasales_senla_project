package eu.senla.userservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.userservice.entity.User;
import eu.senla.userservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final Producer<String, Map<String, UserEvent>> producer;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    public void sendToTopic(User user, String topic) {
        UserEvent event = userMapper.entityToEvent(user);
        log.info("event ... {}", event);
        ProducerRecord<String, Map<String, UserEvent>> producerRecord =
                new ProducerRecord<>(topic, objectMapper.convertValue(event, Map.class));
        producer.send(producerRecord);
        log.info("Sending message ... {}", producerRecord);
    }
}
