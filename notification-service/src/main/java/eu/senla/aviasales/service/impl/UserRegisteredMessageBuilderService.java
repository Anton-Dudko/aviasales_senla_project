package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.Subject.USER_REGISTERED_SUCCESSFULLY;
import eu.senla.aviasales.model.dto.UserRegisteredEventDto;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.MessageBuilderService;
import eu.senla.aviasales.service.util.UserDtoUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class UserRegisteredMessageBuilderService implements MessageBuilderService<UserRegisteredEventDto> {
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;

    @Override
    public void buildAndSend(final UserRegisteredEventDto userRegistrationDto) throws MessagingException {
        UserDtoUtil.setMissedLanguageField(userRegistrationDto);
        Context context = new Context();
        context.setVariable("name", userRegistrationDto.getUserName());
        String html = templateEngine.process("user_registered_" + userRegistrationDto.getUserLanguage(), context);
        emailService.sendEmail(userRegistrationDto.getEmail(), USER_REGISTERED_SUCCESSFULLY, html);
    }
}
