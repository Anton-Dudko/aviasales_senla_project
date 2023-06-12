package eu.senla.aviasales.service;

import eu.senla.aviasales.entity.EmailNotification;
import eu.senla.aviasales.mapper.NotificationMapper;
import eu.senla.aviasales.request.CustomEmailRequest;
import eu.senla.aviasales.response.SendResponse;
import eu.senla.aviasales.response.UserResponse;
import eu.senla.aviasales.service.constant.TopicConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Slf4j
@Service
@Validated
public class SendService {
    private final JavaMailSender javaMailSender;
    private final MessageBuilder messageBuilder;
    private final NotificationMapper notificationMapper;
    private final CrudNotificationService crudNotificationService;
    private final UserserviseService userserviseService;

    public void sendEmail(@Valid EmailNotification emailNotification) {
        log.info("... method sendEmail ");
        Map<String, String> htmlWithSubject = messageBuilder.build(emailNotification);
        emailNotification.setSubject(htmlWithSubject.get("subject"));
        try {
            sendEmail((String) emailNotification.getTemplateVariables().get("email"),
                    htmlWithSubject.get("subject"),
                    htmlWithSubject.get("html"));
            //при повторном отправлении удаляем из БД
            if (emailNotification.getDateFirstSend() != null) {
                crudNotificationService.delete(emailNotification);
            }

        } catch (Exception e) {
            log.warn("... notification don't sent ");
            //сохраняем в БД если до этого там нотификации не было
            if (emailNotification.getDateFirstSend() == null) {
                crudNotificationService.save(emailNotification, e.getMessage());
                sendTroublem(emailNotification);
            } else {
                emailNotification.setCountTrySend(emailNotification.getCountTrySend() + 1);
                crudNotificationService.save(emailNotification, e.getMessage());
            }
        }
    }

    public SendResponse sendEmail(@NotNull String topic,
                                  @Valid CustomEmailRequest customEmailRequest) {
        log.info("... sendEmail");
        sendEmail(notificationMapper.customEmailRequestToEntity(topic, customEmailRequest));
        return SendResponse.builder()
                .subject(customEmailRequest.getSubject())
                .email(customEmailRequest.getEmail())
                .message(customEmailRequest.getSubject())
                .build();
    }

    public SendResponse sendCustomEmail(@Valid CustomEmailRequest customEmailRequest) {
        log.info("... sendCustomEmail");
        return sendEmail(TopicConstants.CUSTOM_EMAIL_TYPE, customEmailRequest);
    }

    private void sendEmail(@NotNull String email,
                           @NotNull String subject,
                           @NotNull String htmlBody) throws MessagingException {
        log.info("... Method sendEmail. Sending email to: {}. Subject: {}", email, subject);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        javaMailSender.send(message);
        log.info("... sent");
    }

    public void sendTroublem(@Valid EmailNotification notSendNotification) {
        log.info("... method sendTroublem");
        List<UserResponse> adminList = userserviseService.getAllAdminsInfo();
        if (!receiverIsAdmin(notSendNotification, adminList)) {
            sendNotificationAboutProblemForAllAdmin(notSendNotification, adminList);
        }
    }

    private void sendNotificationAboutProblemForAllAdmin(EmailNotification notSendNotification, List<UserResponse> adminList) {
        adminList.forEach(admin -> sendEmail(TopicConstants.ERROR_EMAIL_TYPE,
                CustomEmailRequest.builder()
                        .userName(admin.getUsername())
                        .email(admin.getEmail())
                        .subject("ERROR send message ")
                        .body(notSendNotification.toString())
                        .build())
        );
    }

    private boolean receiverIsAdmin(EmailNotification notSendNotification,
                                    List<UserResponse> adminList) {
        return adminList.stream()
                .map(UserResponse::getEmail)
                .toList()
                .contains(notSendNotification.getReceiver());
    }
}
