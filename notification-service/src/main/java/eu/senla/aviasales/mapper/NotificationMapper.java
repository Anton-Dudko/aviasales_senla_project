package eu.senla.aviasales.mapper;

import eu.senla.aviasales.model.entity.EmailNotification;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public interface NotificationMapper {

    EmailNotification recordToEntity(ConsumerRecord<String, Map<String, Object>> consumerRecord);
}
