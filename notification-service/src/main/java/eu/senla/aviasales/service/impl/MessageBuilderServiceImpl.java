package eu.senla.aviasales.service.impl;

import static eu.senla.aviasales.model.constant.message.EmailType.CUSTOM_EMAIL_TYPE;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.config.TemplatesConfig;
import eu.senla.aviasales.exception.TopicNotFoundException;
import eu.senla.aviasales.model.entity.EmailSent;
import eu.senla.aviasales.service.EmailSentService;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.MessageBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageBuilderServiceImpl implements MessageBuilderService {
    private final TemplatesConfig templatesConfig;
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final EmailSentService emailSentService;

    @Override
    public void buildAndSend(String topicName, Map<String, Object> variables) {
        EmailSent emailSent = new EmailSent();
        emailSent.setTemplateType(topicName);
        emailSent.setTemplateVariables(variables);
        emailSent.setSentDate(new Date());
        emailSent.setReceiver((String) variables.get("email"));
        try {
            Map<String, String> builtHtmlWithSubject = buildHtmlWithSubject(topicName, variables);
            emailSent.setSubject(builtHtmlWithSubject.get("subject"));
            emailService.sendEmail((String) variables.get("email"), builtHtmlWithSubject.get("subject"), builtHtmlWithSubject.get("html"));
            emailSent.setIsSent(true);
        } catch (TopicNotFoundException | IOException | MessagingException e) {
            log.warn("Unexpected error: "
                    + e.getMessage()
                    + ". Cannot send the email.");
            emailSent.setIsSent(false);
            emailSent.setErrorMessage(e.getMessage());
        }
        emailSentService.save(emailSent);
    }

    @Override
    public void buildAndSend(final EmailSent emailSent) {
        emailSent.setSentDate(new Date());
        try {
            if (CUSTOM_EMAIL_TYPE.equals(emailSent.getTemplateType())) {
                emailService.sendEmail(emailSent.getReceiver(), emailSent.getSubject(), (String) emailSent.getTemplateVariables()
                        .get("custom_body"));
                emailSent.setIsSent(true);
                emailSentService.update(emailSent);
                return;
            }
            Map<String, String> builtHtmlWithSubject = buildHtmlWithSubject(emailSent.getTemplateType(), emailSent.getTemplateVariables());
            emailService.sendEmail(emailSent.getReceiver(), builtHtmlWithSubject.get("subject"), builtHtmlWithSubject.get("html"));
            emailSent.setIsSent(true);
            emailSentService.update(emailSent);
        } catch (TopicNotFoundException | IOException | MessagingException e) {
            log.warn("Unexpected error: "
                    + e.getMessage()
                    + ". Cannot send the email.");
            emailSent.setErrorMessage(e.getMessage());
            emailSentService.update(emailSent);
        }
    }

    private Map<String, String> buildHtmlWithSubject(String topicName, Map<String, Object> variables)
            throws TopicNotFoundException, IOException {
        setMissedLanguageField(variables);
        Map<String, String> result = new HashMap<>();
        TemplatesConfig.Topic topic = templatesConfig.getTopicByName(topicName);
        result.put("subject", topic.getSubject());
        log.info("Building an email. Receiver: "
                + variables.get("email")
                + ". Subject: "
                + topic.getSubject()
                + ".");
        Context context = new Context();
        context.setVariables(variables);
        context.setVariables(getLocalizationVariables(topic, (String) variables.get("userLanguage")));
        result.put("html", templateEngine.process(topic.getTemplate(), context));
        return result;
    }

    private void setMissedLanguageField(Map<String, Object> dtoVariables) {
        if (dtoVariables.get("userLanguage") == null
                || ((String) dtoVariables.get("userLanguage")).isBlank()
                || !templatesConfig.getLanguages().contains(((String) dtoVariables.get("userLanguage")).toLowerCase())) {
            dtoVariables.put("userLanguage", "en");
        }
    }

    private Map<String, Object> getLocalizationVariables(TemplatesConfig.Topic topic, String language)
            throws IOException {
        return objectMapper.readValue(new ClassPathResource(
                templatesConfig.getLocalizationFolder()
                        + topic.getLocalization()
                        + "_"
                        + language.toLowerCase()
                        + ".json"
        ).getInputStream(), new TypeReference<Map<String, Object>>() {
        });
    }
}
