package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.Subject.USER_REGISTERED_SUCCESSFULLY;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.util.UserDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;
import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class UserRegisteredMessageBuilderService {
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;

    public void buildAndSend(final Map<String, Object> stringObjectMap) throws MessagingException {
        Context context = new Context();
        context.setVariables(stringObjectMap);
        String html = templateEngine.process("user_registered_" + stringObjectMap.get("userLanguage"), context);
        emailService.sendEmail((String) stringObjectMap.get("email"), USER_REGISTERED_SUCCESSFULLY, html);
    }
}
