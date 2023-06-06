package eu.senla.aviasales.mapper;

import eu.senla.aviasales.entity.EmailNotification;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

public interface NotificationMapper {

    EmailNotification recordToEntity(ConsumerRecord<String, Map<String, Object>> consumerRecord);
}
