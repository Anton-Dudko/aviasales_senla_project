package eu.senla.aviasales.service.listener;

import static eu.senla.aviasales.model.constant.Topic.USER_REGISTERED_EVENT;
import static eu.senla.aviasales.model.constant.Topic.USER_RESET_PASSWORD_EVENT;
import eu.senla.aviasales.model.dto.UserDataDto;
import eu.senla.aviasales.model.dto.UserRegisteredEventDto;
import eu.senla.aviasales.model.dto.UserResetPasswordEventDto;
import eu.senla.aviasales.service.MessageBuilderService;
import eu.senla.aviasales.service.validator.ValidatorService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserKafkaListenerService {
    private final MessageBuilderService<UserRegisteredEventDto> userRegisteredEventDtoMessageBuilderService;
    private final MessageBuilderService<UserResetPasswordEventDto> userResetPasswordEventDtoMessageBuilderService;
    private final ValidatorService<UserDataDto> userDataDtoValidatorService;

    @KafkaListener(topicPattern = USER_REGISTERED_EVENT, autoStartup = "true")
    public void userRegisteredListener(ConsumerRecord<String, UserRegisteredEventDto> record) throws Exception {
        UserRegisteredEventDto value = record.value();
        userDataDtoValidatorService.validate(value);
        try {
            userRegisteredEventDtoMessageBuilderService.buildAndSend(value);
        } catch (MessagingException e) {
            log.warn(e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

    @KafkaListener(topicPattern = USER_RESET_PASSWORD_EVENT, autoStartup = "true")
    public void userResetPasswordListener(ConsumerRecord<String, UserResetPasswordEventDto> record) throws Exception {
        UserResetPasswordEventDto value = record.value();
        userDataDtoValidatorService.validate(value);
        try {
            userResetPasswordEventDtoMessageBuilderService.buildAndSend(value);
        } catch (MessagingException e) {
            log.warn(e.getMessage());
            throw new MessagingException(e.getMessage());
        }
    }

}
