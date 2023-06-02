package eu.senla.aviasales.service;

import eu.senla.aviasales.model.entity.EmailSent;

import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface MessageBuilderService {

    void buildAndSend(String topicName ,Map<String, Object> variables);

    void buildAndSend(EmailSent emailSent);

}
