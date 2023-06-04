package eu.senla.aviasales.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface SendService {


    void sendEmail(String to, String subject, String htmlBody) throws MessagingException;

    void sendEmail(ConsumerRecord<String, Map<String, Object>> consumerRecord);
}
