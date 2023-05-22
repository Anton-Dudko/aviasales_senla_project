package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.Subject.USER_REGISTERED_SUCCESSFULLY;
import eu.senla.aviasales.model.dto.UserRegistrationDto;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.UserServiceNotificationService;
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
public class UserServiceNotificationServiceImpl implements UserServiceNotificationService {
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;

    @Override
    public void userRegisteredNotify(final UserRegistrationDto userRegistrationDto) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", userRegistrationDto.getUsername());
        context.setVariable("sign", "Aviasales");
        String html = templateEngine.process("user_registered", context);
        emailService.sendEmail(userRegistrationDto.getEmail(), USER_REGISTERED_SUCCESSFULLY, html);
    }
}
