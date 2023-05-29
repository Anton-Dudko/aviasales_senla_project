package eu.senla.aviasales.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.senla.aviasales.config.TemplatesConfig;
import eu.senla.aviasales.service.EmailService;
import eu.senla.aviasales.service.MessageBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.IOException;
import java.util.Map;
import javax.mail.MessagingException;

/**
 * @author Mikhail.Leonovets
 * @since 05/2023
 */
@RequiredArgsConstructor
@Service
public class MessageBuilderServiceImpl implements MessageBuilderService {
    private final TemplatesConfig templatesConfig;
    private final SpringTemplateEngine templateEngine;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    public void buildAndSend(ConsumerRecord<String, Map<String, Object>> record) throws MessagingException {
        Map<String, Object> dtoVariables = record.value();
        setMissedLanguageField(dtoVariables);
        TemplatesConfig.Topic topic = templatesConfig.getTopicByName(record.topic());
        Context context = new Context();
        context.setVariables(dtoVariables);
        context.setVariables(getLocalizationVariables(topic, (String) dtoVariables.get("userLanguage")));
        String html = templateEngine.process(topic.getTemplate(), context);
        emailService.sendEmail((String) dtoVariables.get("email"), topic.getSubject(), html);
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
