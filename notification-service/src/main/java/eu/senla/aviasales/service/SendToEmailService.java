package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SendToEmailService {
    @Value("${email.send.count}")
    private Integer maxCountSending;
    private final JavaMailSender javaMailSender;
    private final MessageBuilder messageBuilder;
    private final EmailNotificationService service;
    private final AlarmAdminService alarmAdminService;

    public void sendEmail(EmailNotification emailNotification) {
        log.info("... method sendEmail ");
        Map<String, String> htmlWithSubject = messageBuilder.build(emailNotification);
        emailNotification.setSubject(htmlWithSubject.get("subject"));
        try {
            sendEmail((String) emailNotification.getTemplateVariables().get("email"),
                    htmlWithSubject.get("subject"), htmlWithSubject.get("html"));
            //при повторном отправлении удаляем из БД
            if (emailNotification.getDateFirstSend() != null) {
                service.delete(emailNotification);
            }

        } catch (Exception e) {
            log.warn("... notification don't sent ");
            //сохраняем в БД если до этого там нотификации не было
            if (emailNotification.getDateFirstSend() == null) {
                emailNotification.setDateFirstSend(LocalDate.now());
                emailNotification.setException(e.getMessage());
                service.save(emailNotification);
                alarmAdminService.sendTroublem(emailNotification, e.getMessage());
            }
        }
    }

    public void sendEmail(String email, String subject, String htmlBody) throws MessagingException {
        log.info("... Method sendEmail. Sending email to: {}. Subject: {}", email, subject);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
        log.info("... sent");
    }

    @Scheduled(fixedDelayString = "${email.send.interval}")
    public void sendWrongLetter() {
        log.info("... method sendWrongLetter");
        List<EmailNotification> list = service.findAll().getEmailNotificationList();
        if (!list.isEmpty()) {
            list.forEach(e -> {
                sendEmail(e);
                e.setCountTrySend(e.getCountTrySend() + 1);
                //проверка на мах число попыток отправлений
                if (e.getCountTrySend() >= maxCountSending) {
                    service.delete(e);
                }
            });
        }
    }
}
