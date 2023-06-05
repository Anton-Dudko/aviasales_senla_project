package eu.senla.aviasales.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.config.TemplatesConfig;
import eu.senla.aviasales.exception.custom.TopicNotFoundException;
import eu.senla.aviasales.model.entity.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MessageBuilder {
    private final TemplatesConfig templatesConfig;
    private final SpringTemplateEngine templateEngine;

    private final ObjectMapper objectMapper;

    public Map<String, String> build(EmailNotification emailNotification) {
        log.info("... method build");
        try {
            setMissedLanguageField(emailNotification.getTemplateVariables());
            Map<String, String> result = new HashMap<>();
            TemplatesConfig.Topic topic = templatesConfig.getTopicByName(emailNotification.getTemplateType());
            result.put("subject", topic.getSubject());
            log.info("Building an email. Receiver: "
                    + emailNotification.getTemplateVariables().get("email")
                    + ". Subject: "
                    + topic.getSubject()
                    + ".");
            Context context = new Context();
            context.setVariables(emailNotification.getTemplateVariables());
            context.setVariables(getLocalizationVariables(topic, (String) emailNotification.getTemplateVariables().get("userLanguage")));
            result.put("html", templateEngine.process(topic.getTemplate(), context));
            return result;
        } catch (TopicNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

//    public void buildAndSend(final EmailNotification emailNotification) {
//        emailNotification.setDateFirstSend(LocalDate.now());
//        try {
//            log.info("... templateVariables " + emailNotification.getTemplateVariables());
//            Map<String, String> builtHtmlWithSubject = buildHtmlWithSubject(emailNotification.getTemplateType(), emailNotification.getTemplateVariables());
//            sendService.sendEmail(emailNotification.getReceiver(), builtHtmlWithSubject.get("subject"), builtHtmlWithSubject.get("html"));
//            emailNotificationRepository.delete(emailNotification);
//        } catch (TopicNotFoundException | IOException | MessagingException e) {
//            log.warn("Unexpected error: "
//                    + e.getMessage()
//                    + ". Cannot send the email.");
//        }
//    }

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
        ).getInputStream(), new TypeReference<>() {
        });
    }
}
