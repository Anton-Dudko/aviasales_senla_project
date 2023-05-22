package eu.senla.aviasales.service.listener;

import static eu.senla.aviasales.exception.constant.IncorrectDataExceptionMessage.EMAIL_CANNOT_BE_EMPTY;
import static eu.senla.aviasales.exception.constant.IncorrectDataExceptionMessage.NAME_CANNOT_BE_NULL;
import static eu.senla.aviasales.exception.constant.IncorrectDataExceptionMessage.USER_DATA_CANNOT_BE_NULL;
import static eu.senla.aviasales.service.listener.constant.Topic.USER_REGISTERED_EVENT;
import eu.senla.aviasales.dto.UserRegistrationDto;
import eu.senla.aviasales.exception.IncorrectDataException;
import eu.senla.aviasales.service.UserServiceNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.util.Strings;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class UserListenerService {
    private final UserServiceNotificationService userServiceNotificationService;

    @KafkaListener(topicPattern = USER_REGISTERED_EVENT, autoStartup = "true")
    public void listenToNewUser(ConsumerRecord<String, UserRegistrationDto> record) throws IncorrectDataException {
        UserRegistrationDto value = record.value();
        if (value == null) {
            throw new IncorrectDataException(USER_DATA_CANNOT_BE_NULL);
        } else if (value.getEmail() == null || Strings.isEmpty(value.getEmail())) {
            throw new IncorrectDataException(EMAIL_CANNOT_BE_EMPTY);
        } else if (value.getUsername() == null || Strings.isEmpty(value.getUsername())) {
            throw new IncorrectDataException(NAME_CANNOT_BE_NULL);
        }

        try {
            userServiceNotificationService.userRegisteredNotify(value);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
