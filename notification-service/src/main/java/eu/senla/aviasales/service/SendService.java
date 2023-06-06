package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;

import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface SendService {

    void sendEmail(String to, String subject, String htmlBody) throws MessagingException;

    void sendEmail(EmailNotification emailNotification);
}
