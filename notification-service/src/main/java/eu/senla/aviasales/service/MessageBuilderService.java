package eu.senla.aviasales.service;

import jakarta.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface MessageBuilderService<T> {

    void buildAndSend(T dto) throws MessagingException;

}
