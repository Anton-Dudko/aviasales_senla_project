package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.service.MessageBuilder;
import eu.senla.aviasales.service.SendService;
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

@RequiredArgsConstructor
@Slf4j
@Service
public class SendToEmailServiceImplTest implements SendService {
    @Value("${email.send.count}")
    private Integer maxCountSending;

    private final JavaMailSender javaMailSender;
    private final MessageBuilder messageBuilder;
    private final EmailNotificationService service;

    private int tempCount = 0;
    private int countException = 3;

    @Override
    public void sendEmail(EmailNotification emailNotification) {
        Map<String, String> htmlWithSubject = messageBuilder.build(emailNotification);
        emailNotification.setSubject(htmlWithSubject.get("subject"));
        try {
            sendEmail((String) emailNotification.getTemplateVariables().get("email"),
                    htmlWithSubject.get("subject"), htmlWithSubject.get("html"));
            if (emailNotification.getDateFirstSend() != null) {
                log.info("... DELETE FROM DB AFTER SENDING");
                service.delete(emailNotification);
            }
        } catch (Exception e) {
            log.warn("... EXCEPTION {}", e.getMessage());
            if (emailNotification.getDateFirstSend() == null) {
                log.info("... SAVE IN DB FIRST TIME");
                emailNotification.setDateFirstSend(LocalDate.now());
                service.save(emailNotification);
            }
        }
    }

    @Override
    public void sendEmail(String email, String subject, String htmlBody) throws MessagingException {
        while (tempCount < countException) {
            log.warn("... COUNT_EXCEPTION {}", tempCount);
            throw new RuntimeException("***************************************");
        }

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
    }

    @Scheduled(fixedDelayString = "${email.send.interval}")
    public void sendWrongLetter() {
        log.info("... method sendWrongLetter");
        List<EmailNotification> list = service.findAll();
        if (!list.isEmpty()) {
            tempCount++;
            log.info("... THERE ARE SOME NOT SENT NOTIFICATION");
            for (EmailNotification en : list) {
                log.info(en.toString());
            }
            list.forEach(e -> {
                sendEmail(e);
                e.setCountTrySend(e.getCountTrySend() + 1);
                if (e.getCountTrySend() >= maxCountSending) {
                    service.delete(e);
                }
            });
        }
        if (list.isEmpty()) {
            tempCount = 0;
        }
        log.info("... exit method sendWrongLetter");
    }
}
