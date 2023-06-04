package eu.senla.aviasales.service.impl;

import eu.senla.aviasales.mapper.NotificationMapper;
import eu.senla.aviasales.model.entity.EmailNotification;
import eu.senla.aviasales.repository.EmailNotificationRepository;
import eu.senla.aviasales.service.SendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SendToEmailServiceImpl implements SendService {

    private final NotificationMapper notificationMapper;
    private final JavaMailSender javaMailSender;
    private final MessageBuilder messageBuilder;
    private final EmailNotificationRepository repository;


    @Override
    public void sendEmail(ConsumerRecord<String, Map<String, Object>> consumerRecord) {
        EmailNotification emailNotification = notificationMapper.recordToEntity(consumerRecord);
        Map<String, String> htmlWithSubject = messageBuilder.build(emailNotification);
        emailNotification.setSubject(htmlWithSubject.get("subject"));
        try {
            sendEmail((String) emailNotification.getTemplateVariables().get("email"),
                    htmlWithSubject.get("subject"), htmlWithSubject.get("html"));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendEmail(String email, String subject, String htmlBody) throws MessagingException {
        log.info("Sending email to: " + email
                + ". Subject: "
                + subject);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
    }
}
