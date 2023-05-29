package eu.senla.aviasales.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;
import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface MessageBuilderService {

    void buildAndSend(ConsumerRecord<String, Map<String, Object>> record) throws MessagingException;

}
