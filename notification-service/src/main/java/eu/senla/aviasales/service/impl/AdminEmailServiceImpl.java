package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.exception.custom.EmailSentNotFoundException;
import eu.senla.aviasales.model.dto.CustomEmailDto;
import eu.senla.aviasales.model.entity.EmailSent;
import eu.senla.aviasales.service.AdminEmailService;
import eu.senla.aviasales.service.EmailSentService;
import eu.senla.aviasales.service.MessageBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Map;

import static eu.senla.aviasales.model.constant.EmailType.CUSTOM_EMAIL_TYPE;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class AdminEmailServiceImpl implements AdminEmailService {
    private final SmtpEmailService smtpEmailService;
    private final EmailSentService emailSentService;
    private final MessageBuilderService messageBuilderService;

    @Override
    public void sendCustomEmail(final CustomEmailDto customEmailDto) {
        EmailSent emailSent = new EmailSent(customEmailDto.getTo(),
                customEmailDto.getSubject(),
                CUSTOM_EMAIL_TYPE,
                Map.of("custom_body", customEmailDto.getBody()),
                true);
        try {
            smtpEmailService.sendEmail(customEmailDto.getTo(), customEmailDto.getSubject(), customEmailDto.getBody());
        } catch (MessagingException e) {
            log.warn(e.getMessage());
            emailSent.setIsSent(false);
        }
        emailSentService.save(emailSent);
    }

    @Override
    public void sendAgain(final Long id) throws EmailSentNotFoundException {
        messageBuilderService.buildAndSend(emailSentService.findById(id));
    }
}
