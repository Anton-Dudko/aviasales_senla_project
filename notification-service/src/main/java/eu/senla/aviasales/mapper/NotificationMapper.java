package eu.senla.aviasales.mapper;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.request.CustomEmailRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.validation.constraints.NotNull;
import java.util.Map;

public interface NotificationMapper {

    EmailNotification recordToEntity(@NotNull ConsumerRecord<String, Map<String, Object>> consumerRecord);

    EmailNotification customEmailRequestToEntity(@NotNull String topic,
                                                 @NotNull CustomEmailRequest customEmailRequest);
}
