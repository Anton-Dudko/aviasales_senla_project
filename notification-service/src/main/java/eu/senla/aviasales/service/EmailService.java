package eu.senla.aviasales.service;

import jakarta.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface EmailService {

    void sendEmail(String to, String subject, String htmlBody) throws MessagingException;

}
