package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.Subject.USER_RESET_PASSWORD;
import eu.senla.aviasales.model.dto.UserResetPasswordEventDto;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.MessageBuilderService;
import eu.senla.aviasales.service.util.UserDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class UserResetPasswordMessageBuilderService implements MessageBuilderService<UserResetPasswordEventDto> {
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final UserDtoUtil userDtoUtil;

    @Override
    public void buildAndSend(final UserResetPasswordEventDto dto) throws MessagingException {
        userDtoUtil.setMissedLanguageField(dto);
        Context context = new Context();
        context.setVariable("name", dto.getUserName());
        context.setVariable("newPassword", dto.getNewPassword());
        String html = templateEngine.process("user_reset_password_" + dto.getUserLanguage(), context);
        emailService.sendEmail(dto.getEmail(), USER_RESET_PASSWORD, html);
    }
}
