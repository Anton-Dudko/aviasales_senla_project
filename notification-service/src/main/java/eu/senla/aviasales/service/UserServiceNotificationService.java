package eu.senla.aviasales.service;

import eu.senla.aviasales.model.dto.UserRegistrationDto;
import jakarta.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
public interface UserServiceNotificationService {

    void userRegisteredNotify(UserRegistrationDto userRegistrationDto) throws MessagingException;

}
